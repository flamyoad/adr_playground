package com.flamyoad.basicpaginationconcatadapter

import com.flamyoad.basicpaginationconcatadapter.common.State
import com.flamyoad.basicpaginationconcatadapter.model.NumberPojo
import kotlinx.coroutines.delay

class NumberRepository {

    suspend fun getNumbers(seedValue: Int): State<List<NumberPojo>> {
        // Mimic api call by adding delay
        delay(2500)

        // Mimic api failure
        if ((0..5).random() == 5) {
            return State.Error()
        }

        val result = (seedValue until seedValue + 20).map {
            NumberPojo(
                value = (it * seedValue).toString(),
                seed = seedValue
            )
        }.toList()

        return State.Success(result)
    }
}