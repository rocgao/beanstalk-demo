import com.dinstone.beanstalkc.BeanstalkClientFactory
import com.dinstone.beanstalkc.Configuration
import kotlinx.coroutines.runBlocking
import org.rocgao.beanstalkdemo.extension.customTime
import org.rocgao.beanstalkdemo.extension.joinWithComma
import java.time.LocalDateTime

const val TUBE_DEMO_NAME = "tube_demo1"

fun main(args: Array<String>) = runBlocking {

    // 生成配置对象
    val config = Configuration().apply {
        serviceHost = "127.0.0.1"
        servicePort = 11300
    }

    // 创建client
    val factory = BeanstalkClientFactory(config)
    val client = factory.createBeanstalkClient().apply { useTube(TUBE_DEMO_NAME) }

    // 生产Job
    client.putJob(1024, // 优先级
        30, // Delay时间(秒）
        0, // TTR
        "Job-${LocalDateTime.now().customTime()}".toByteArray())
    println("${LocalDateTime.now().customTime()}:job has been put")

    // 设置订阅的Tube
    client.watchTube(TUBE_DEMO_NAME)

    // 列出相关信息
    println("Tube used:${client.listTubeUsed()}")
    println("All watched tubes${client.listTubeWatched().joinWithComma()}")
    println("All tubes:${client.listTubes().joinWithComma()}")

    // 消费job
    while (true) {
        val job = client.reserveJob(1000) ?: continue
        println("${LocalDateTime.now().customTime()} receive: ${job.id} ${job.data.decodeToString()}")
        client.deleteJob(job.id)
//        break
    }
    client.close()

}



