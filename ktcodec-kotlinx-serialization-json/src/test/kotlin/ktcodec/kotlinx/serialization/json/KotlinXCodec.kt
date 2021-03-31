package ktcodec.kotlinx.serialization.json

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.serialization.Serializable
import ktcodec.TimeLineEvent
import ktcodec.create

@Serializable
data class Sample(val sample1: String, val sample2: String)

val sampleArb = arbitrary { rs ->
    Sample(
        Arb.string().next(rs),
        Arb.string().next(rs)
    )
}

class KotlinXCodec : DescribeSpec({
    describe("kotlinx serialization codec") {
        it("can encode and decode data class") {

            checkAll(sampleArb) { sample ->
                val serializer = Sample.serializer()

                val (encode, decode) = create(serialize(serializer), deserialize(serializer))


                val eventData = encode(sample)
                val timeLineEvent = TimeLineEvent(eventData, 0)
                val eventEncoded = decode(timeLineEvent)

                sample shouldBe eventEncoded
            }

        }
    }
})
