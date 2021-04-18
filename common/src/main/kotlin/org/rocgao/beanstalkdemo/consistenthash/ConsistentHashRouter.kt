package org.rocgao.beanstalkdemo.consistenthash

import java.security.MessageDigest
import java.util.*

class ConsistentHashRouter<T>(
    pNodes: Collection<T>,
    vNodeCount: Int,
    private val hashFunc: HashFunction,
) where T : Node {
    constructor(pNodes: Collection<T>, vNodeCount: Int) : this(pNodes, vNodeCount, MD5Hash())

    private val ring = TreeMap<Long, VirtualNode<T>>()

    init {
        for (pNode in pNodes) {
            addNode(pNode, vNodeCount)
        }
    }

    fun addNode(pNode: T, vNodeCount: Int) {
        if (vNodeCount < 0) {
            throw IllegalArgumentException("illegal virtual node counts :$vNodeCount")
        }
        val existingReplicas = getExistingReplicas(pNode)
        for (i in 0..existingReplicas) {
            val vNode = VirtualNode<T>(pNode, i + existingReplicas)
            ring[hashFunc.hash(vNode.getKey())] = vNode
        }
    }

    fun removeNode(pNode: T) {
        val it = ring.keys.iterator()
        while (it.hasNext()) {
            val key = it.next()
            val virtualNode = ring[key]!!
            if (virtualNode.isVirtualNodeOf(pNode)) {
                it.remove()
            }
        }
    }

    fun routeNode(objectKey: String): T? {
        if (ring.isEmpty()) {
            return null
        }

        val hashVal = hashFunc.hash(objectKey)
        val tailMap = ring.tailMap(hashVal)
        var nodeHashVal = ring.firstKey()
        if (tailMap.isNotEmpty()) {
            nodeHashVal = tailMap.firstKey()
        }
        return ring[nodeHashVal]?.physicalNode
    }

    fun getExistingReplicas(pNode: T): Int = ring.values.count { vNode -> vNode.isVirtualNodeOf(pNode) }

    private class MD5Hash : HashFunction {
        private val instance = MessageDigest.getInstance("MD5")
        override fun hash(key: String): Long {
            instance.reset()
            instance.update(key.toByteArray())
            val digest = instance.digest()
            var h = 0L
            for (i in 0..3) {
                h = h shl 8 // h <<= 8
                h = (digest[i].toLong() and 0xFF) or h
            }
            return h
        }
    }


}