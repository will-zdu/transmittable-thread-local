package com.alibaba.ttl

import com.alibaba.support.junit.conditional.ConditionalIgnoreRule
import com.alibaba.support.junit.conditional.ConditionalIgnoreRule.ConditionalIgnore
import com.alibaba.support.junit.conditional.IsAgentRun
import com.alibaba.ttl.threadpool.TtlExecutors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors

private const val hello = "hello"
private val defaultValue = "${Date()} ${Math.random()}"

class InheritableTest {
    @Rule
    @JvmField
    val rule = ConditionalIgnoreRule()

    @Test
    fun inheritable() {
        val threadPool = Executors.newCachedThreadPool()
        val ttl = TransmittableThreadLocal<String?>()
        ttl.set(hello)

        val callable = Callable { ttl.get() } // NO TtlWrapper(TtlCallable) here!!

        // get "hello" value is transmitted by InheritableThreadLocal function!
        // NOTE: newCachedThreadPool create thread lazily
        assertEquals(hello, threadPool.submit(callable).get())

        // current thread's TTL must be exist when using DisableInheritableThreadFactory
        assertEquals(hello, ttl.get())

        threadPool.shutdown()
    }

    @Test
    @ConditionalIgnore(condition = IsAgentRun::class)
    fun disableDisableInheritableThreadFactory() {
        val threadPool = Executors.newCachedThreadPool(TtlExecutors.getDefaultDisableInheritableThreadFactory())
        val ttl = TransmittableThreadLocal<String?>()
        ttl.set(hello)

        val callable = Callable { ttl.get() } // NO TtlWrapper(TtlCallable) here!!

        // when ttl agent is loaded, Callable is wrapped when submit,
        // so here value is "hello" transmitted by TtlCallable wrapper
        // IGNORE this test case when TtlAgent is run.
        assertNull(threadPool.submit(callable).get())

        // current thread's TTL must be exist when using DisableInheritableThreadFactory
        assertEquals(hello, ttl.get())

        threadPool.shutdown()
    }


    @Test
    @ConditionalIgnore(condition = IsAgentRun::class)
    fun disableDisableInheritableThreadFactory_TTL_with_initialValue() {
        val threadPool = Executors.newCachedThreadPool(TtlExecutors.getDefaultDisableInheritableThreadFactory())
        val ttl = object : TransmittableThreadLocal<String>() {
            override fun initialValue(): String = defaultValue
            override fun childValue(parentValue: String): String = initialValue()
        }
        ttl.set(hello)

        val callable = Callable { ttl.get() } // NO TtlWrapper(TtlCallable) here!!

        // when ttl agent is loaded, Callable is wrapped when submit,
        // so here value is "hello" transmitted by TtlCallable wrapper
        // IGNORE this test case when TtlAgent is run.
        assertEquals(defaultValue, threadPool.submit(callable).get())

        // current thread's TTL must be exist when using DisableInheritableThreadFactory
        assertEquals(hello, ttl.get())

        threadPool.shutdown()
    }

    @Test
    @ConditionalIgnore(condition = IsAgentRun::class)
    fun disableInheritable() {
        val threadPool = Executors.newCachedThreadPool()
        val ttl = object : TransmittableThreadLocal<String?>() {
            override fun childValue(parentValue: String?): String? = initialValue()
        }
        ttl.set(hello)

        val callable = Callable { ttl.get() } // NO TtlWrapper(TtlCallable) here!!

        // when ttl agent is loaded, Callable is wrapped when submit,
        // so here value is "hello" transmitted by TtlCallable wrapper
        // IGNORE this test case when TtlAgent is run.
        assertNull(threadPool.submit(callable).get())

        // current thread's TTL must be exist when using DisableInheritableThreadFactory
        assertEquals(hello, ttl.get())

        threadPool.shutdown()
    }

    @Test
    @ConditionalIgnore(condition = IsAgentRun::class)
    fun disableInheritable_TTL_with_initialValue() {
        val threadPool = Executors.newCachedThreadPool()
        val ttl = object : TransmittableThreadLocal<String>() {
            override fun initialValue(): String = defaultValue
            override fun childValue(parentValue: String): String = initialValue()
        }
        ttl.set(hello)

        val callable = Callable { ttl.get() } // NO TtlWrapper(TtlCallable) here!!

        // when ttl agent is loaded, Callable is wrapped when submit,
        // so here value is "hello" transmitted by TtlCallable wrapper
        // IGNORE this test case when TtlAgent is run.
        assertEquals(defaultValue, threadPool.submit(callable).get())

        // current thread's TTL must be exist when using DisableInheritableThreadFactory
        assertEquals(hello, ttl.get())

        threadPool.shutdown()
    }
}
