package org.rocgao.beanstalkdemo.consistenthash

class VirtualNode<T>(val physicalNode: T, private val replicaIndex: Int) : Node where T : Node {
    override fun getKey(): String = "${physicalNode.getKey()}-${replicaIndex}"
    fun isVirtualNodeOf(pNode: T): Boolean = physicalNode.getKey() == pNode.getKey()
}