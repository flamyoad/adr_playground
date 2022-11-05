package main

import (
	"encoding/json"
	"fmt"
	"net/http"
	"regexp"
	"sync"
)

var (
	listUserRe   = regexp.MustCompile(`^\/users[\/]*$`)
	getUserRe    = regexp.MustCompile(`^\/users\/(\d+)$`)
	createUserRe = regexp.MustCompile(`^\/users[\/]*$`)
)

// if both field are same key `json:"id"`, somehow json.Marshal() returns empty response {}, Wtf!
type user struct {
	ID   string `json:"id"`
	Name string `json:"name"`
}

type datastore struct {
	m             map[string]user
	*sync.RWMutex // Anonymous field. This will make methods of this class promoted to parent class (datastore). Is like inheritance.
}

// https://medium.com/rungo/structures-in-go-76377cc106a2

type userHandler struct {
	store *datastore
}

func (h *userHandler) ServeHTTP(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("content-type", "application/json")
	switch {
	case r.Method == http.MethodGet && listUserRe.MatchString(r.URL.Path):
		h.List(w, r)
		return
	case r.Method == http.MethodGet && getUserRe.MatchString(r.URL.Path):
		h.Get(w, r)
		return
	case r.Method == http.MethodPost && createUserRe.MatchString(r.URL.Path):
		h.Create(w, r)
		return
	default:
		notFound(w, r)
		return
	}
}

func (h *userHandler) List(w http.ResponseWriter, r *http.Request) {
	/*
		if in here, we invoke RLock(),
		then after that invoke Unlock() , notice the absence of R.
		it will throw fatal error: sync: Unlock of unlocked RWMutex!!!!!!
	*/
	h.store.RLock()
	users := make([]user, 0, len(h.store.m))
	for _, v := range h.store.m {
		users = append(users, v)
	}
	h.store.RUnlock()

	jsonBytes, err := json.Marshal(users)
	if err != nil {
		internalServerError(w, r)
		return
	}
	fmt.Println(jsonBytes)
	w.WriteHeader(http.StatusOK)
	w.Write(jsonBytes)
}

func (h *userHandler) Get(w http.ResponseWriter, r *http.Request) {
	matches := getUserRe.FindStringSubmatch(r.URL.Path)
	if len(matches) < 2 {
		notFound(w, r)
		return
	}

	h.store.RLock()
	u, ok := h.store.m[matches[1]]
	h.store.RUnlock()

	if !ok {
		w.WriteHeader(http.StatusNotFound)
		w.Write([]byte("user not found"))
		return
	}

	jsonBytes, err := json.Marshal(u)
	if err != nil {
		internalServerError(w, r)
		return
	}

	w.WriteHeader(http.StatusOK)
	w.Write(jsonBytes)
}

// Instance method - func <class> <method name> <method args> <return type>(Can be omitted)
func (h *userHandler) Create(w http.ResponseWriter, r *http.Request) {
	var u user
	if err := json.NewDecoder(r.Body).Decode(&u); err != nil {
		internalServerError(w, r)
		return
	}

	h.store.Lock()
	h.store.m[u.ID] = u
	h.store.Unlock()

	jsonBytes, err := json.Marshal(u)
	if err != nil {
		internalServerError(w, r)
		return
	}

	w.WriteHeader(http.StatusOK)
	w.Write(jsonBytes)

}

func internalServerError(w http.ResponseWriter, r *http.Request) {
	w.WriteHeader(http.StatusInternalServerError)
	w.Write([]byte("Internal server error"))
}

func notFound(w http.ResponseWriter, r *http.Request) {
	w.WriteHeader(http.StatusNotFound)
	w.Write([]byte("not found"))
}

func main() {
	mux := http.NewServeMux()
	userH := &userHandler{
		store: &datastore{
			m: map[string]user{
				"1": {ID: "1", Name: "Bob"},
			},
			RWMutex: &sync.RWMutex{},
		},
	}

	mux.Handle("/users", userH)
	mux.Handle("/users/", userH)

	http.ListenAndServe("localhost:8080", mux)
}

// https://golang.cafe/blog/golang-rest-api-example.html
