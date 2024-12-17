import me.azakharov.kit.buildStateMachine
import kotlin.test.Test
import kotlin.test.assertEquals

typealias Vector2 = Array<Float>
val VectorZero: Vector2 get () = arrayOf(0f, 0f)
var Vector2.x: Float
    get() = this[0]
    set(value) { this[0] = value }
var Vector2.y
    get() = this[1]
    set(value) { this[1] = value }

val Vector2.isZero: Boolean get() = x == 0f && y == 0f
fun Vector2.toString() = "$x x $y"

operator fun Vector2.minus(other: Vector2): Vector2 = arrayOf( this.x - other.x, this.y - other.y )

class SmokeTest {

    // context
    data class Car(
        var pos: Vector2 = VectorZero, // mutableVector TBD
        var accel: Vector2 = arrayOf(0f, 0f),
        var handBrake: Boolean = true,
        var mass: Float = 1f,
    ) {
        var prevPos = pos
        val velocity get() = prevPos - pos
        fun heavyUpdate(dt: Float) {

            val vel = velocity

            prevPos = pos

            pos.x += vel.x + accel.x * dt * dt
            pos.y += vel.y + accel.y * dt * dt
        }

        fun addForce() {

        }

    }

    // what happens
    enum class CAREVENTS {
        IGNITION, GAS, BREAK, IDLE, LEFT, RIGHT
    }

    @Test
    fun simple() {

        val car = Car()

        val fsm = buildStateMachine<CAREVENTS>("parked") {
            state("parked") {
                onEnter {
                    println("parked at ${car.pos}")
                    car.handBrake = true
                }
                onExit {
                    car.handBrake = false
                }

                edge("idle") {
                    validWhen { this == CAREVENTS.IGNITION }
                }
            }

            state("idle") {
                onEnter {
                    println("ready to go ${car.pos}")
                    car.accel = arrayOf(0f, 0f)
                }
                edge("moving") {
                    validWhen { this == CAREVENTS.GAS }
                }
            }

            state("moving") {
                onEnter {
                    println("warping")
                }
                edge("idle") {
                    validWhen {
                        this == CAREVENTS.BREAK
                    }
                }
                onUpdate {
                    car.heavyUpdate(1f)
                }
            }

        }

        fsm.update(CAREVENTS.IGNITION)
        assertEquals("idle", fsm.currentState?.name)
        fsm.update(CAREVENTS.IGNITION)
        assertEquals("idle", fsm.currentState?.name)
        fsm.update(CAREVENTS.GAS)
        assertEquals("moving", fsm.currentState?.name)
        fsm.update(CAREVENTS.IDLE)
        assertEquals("moving", fsm.currentState?.name)
        println( car.pos )

    }

}
