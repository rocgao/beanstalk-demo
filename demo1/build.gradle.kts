plugins {
    id("org.rocgao.beanstalkdemo.kotlin-application-conventions")
}

dependencies{
    implementation(project(":common"))
}

application{
    mainClass.set("MainKt")
}
