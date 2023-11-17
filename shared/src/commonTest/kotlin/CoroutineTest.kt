import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class CoroutineTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun name() = runTest {
        val test = flowOf(1, 2, 3, 4).flatMapLatest {
            flow {
                delay(1000)
                emit(it)
            }
        }.single()

        println(test)
    }
}