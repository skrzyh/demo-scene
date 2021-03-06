package io.confluent.developer.dsl

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kotlin.KSerdes.producedWith
import org.apache.kafka.streams.kotlin.count
import org.apache.kafka.streams.kotlin.createTopology
import org.apache.kafka.streams.kotlin.groupByKey
import org.apache.kafka.streams.kotlin.kstream
import java.util.*

fun main() {
  val topology = createTopology {
    kstream<String, Long>(listOf("A", "B")) {
      groupByKey {
        count {
          toStream()
        }
      }
    }.to("group-by-counts",
            producedWith<String, Long>())
  }

  // just debug
  println(topology.describe().toString())

  KafkaStreams(topology, streamsProperties()).start()
}

private fun streamsProperties(): Properties {
  val properties = Properties()
  properties.putAll(mapOf(
          ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
          StreamsConfig.APPLICATION_ID_CONFIG to "kotlin-dsl-ftw"
  ))
  return properties
}
