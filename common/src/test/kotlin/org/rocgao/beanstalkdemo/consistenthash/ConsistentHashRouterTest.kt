package org.rocgao.beanstalkdemo.consistenthash

import kotlin.test.Test
import kotlin.test.assertNotNull

class ConsistentHashRouterTest {

    private data class TestNode(private val ip: String, private val port: Int) : Node {
        override fun getKey(): String = "${ip}-${port}"
    }

    @Test
    fun smokeTest() {
        val router =
            ConsistentHashRouter(arrayListOf(TestNode("127.0.0.1", 8000), TestNode("127.0.0.1", 8001)), 10)

        repeat(10) {
            val tube = "tube_demo${it}"
            val node = router.routeNode(tube)
            println("tube:${tube} -> node${it}:${node}")
            assertNotNull(node)
        }
    }
}