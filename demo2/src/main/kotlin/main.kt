import com.dinstone.beanstalkc.*
import org.rocgao.beanstalkdemo.consistenthash.ConsistentHashRouter
import org.rocgao.beanstalkdemo.consistenthash.Node
import org.rocgao.beanstalkdemo.extension.customTime
import java.time.LocalDateTime

fun main() {
    val tube = "tube_demo4"
    val producer = BeanstalkHelper.createProducer(tube)
    producer.putJob(1024, 10, 0, "Job-${LocalDateTime.now().customTime()}".toByteArray())
    producer.close()

    val consumer = BeanstalkHelper.createConsumer(tube)
    while (true) {
        val job = consumer.reserveJob(1000) ?: continue
        println("${LocalDateTime.now().customTime()} receive: ${job.id} ${job.data.decodeToString()}")
        consumer.deleteJob(job.id)
    }
}

object BeanstalkHelper {
    private val router: ConsistentHashRouter<BeanstalkNode>

    init {
        val nodes = arrayListOf(
            BeanstalkNode("127.0.0.1", 11300),
            BeanstalkNode("127.0.0.1", 11301),
            BeanstalkNode("127.0.0.1", 11302),
        )
        router = ConsistentHashRouter(nodes, 10)
    }

    fun createProducer(tube: String): JobProducer {
        val node = router.routeNode(tube)!!
        println("producer -> tube:${tube} connect to ${node.serviceHost}:${node.servicePort}")
        return BeanstalkClientFactory(node).createJobProducer(tube)
    }

    fun createConsumer(tube: String): JobConsumer {
        val node = router.routeNode(tube)!!
        println("consumer -> tube:${tube} connect to ${node.serviceHost}:${node.servicePort}")
        return BeanstalkClientFactory(node).createJobConsumer(tube)
    }
}

class BeanstalkNode(host: String, port: Int) : Configuration(), Node {
    init {
        serviceHost = host
        servicePort = port
    }

    override fun getKey(): String = "${serviceHost}-${servicePort}"
}