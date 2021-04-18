package org.rocgao.beanstalkdemo.consistenthash

interface HashFunction {
    fun hash(key: String): Long
}