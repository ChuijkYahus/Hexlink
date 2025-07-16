package jempasam.hexlink.utils

import com.mojang.serialization.Lifecycle
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.SimpleRegistry
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.util.math.random.Random

class LoadableRegistry<T>(val _key: RegistryKey<out Registry<T>>, lifecycle: Lifecycle): Registry<T> {

    private var inner = SimpleRegistry<T>(_key, lifecycle, false)
    private var locked = true


    fun clear(){
        inner = SimpleRegistry(key, lifecycle, false)
        locked=false
    }

    fun lock(){
        locked=true
    }

    fun register(id: Identifier, value: T) {
        if (locked) throw IllegalStateException("Registry is locked")
        Registry.register(inner, id, value)
    }

    override fun iterator() = inner.iterator()

    override fun get(id: Identifier?) = inner.get(id)

    override fun get(index: Int) = inner.get(index)

    override fun size() = inner.size()

    override fun getKey(): RegistryKey<out Registry<T>> {
        return _key
    }

    override fun getLifecycle() = inner.lifecycle

    override fun getIds() = inner.ids
    override fun getEntrySet() = inner.entrySet
    override fun getKeys() = inner.keys

    override fun getRandom(random: Random?) = inner.getRandom(random)

    override fun containsId(id: Identifier?) = inner.containsId(id)

    override fun freeze() = inner.freeze()

    override fun get(key: RegistryKey<T>?) = inner.get(key)

    override fun getId(value: T) = inner.getId(value)
    override fun getKey(entry: T) = inner.getKey(entry)
    override fun getRawId(value: T?) = inner.getRawId(value)

    override fun getEntry(rawId: Int) = inner.getEntry(rawId)
    override fun getEntry(value: T) = inner.getEntry(value)
    override fun getEntry(key: RegistryKey<T>) = inner.getEntry(key)

    override fun getOrCreateEntryList(tag: TagKey<T>?) = inner.getOrCreateEntryList(tag)
    override fun getEntryList(tag: TagKey<T>?) = inner.getEntryList(tag)

    override fun streamEntries() = inner.streamEntries()
    override fun streamTagsAndEntries() = inner.streamTagsAndEntries()
    override fun streamTags() = inner.streamTags()

    override fun clearTags() = inner.clearTags()

    override fun populateTags(tagEntries: MutableMap<TagKey<T>, MutableList<RegistryEntry<T>>>?) = inner.populateTags(tagEntries)

    override fun getEntryOwner() = inner.entryOwner

    override fun getReadOnlyWrapper() = inner.readOnlyWrapper

    override fun createEntry(value: T) = inner.createEntry(value)

    override fun contains(key: RegistryKey<T>?) = inner.contains(key)

    override fun getEntryLifecycle(entry: T) = inner.getEntryLifecycle(entry)

}