package com.dicoding.picodiploma.loginwithanimation

import com.dicoding.picodiploma.loginwithanimation.data.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                photoUrl = "https://dummyurl.com/photo$i.jpg",
                createdAt = "2024-12-16T12:14:${i % 60}",
                name = "Name $i",
                description = "Description $i",
                lon = -122.083922 + i * 0.001,
                id = "story-$i",
                lat = 37.4220936 + i * 0.001
            )
            items.add(story)
        }
        return items
    }
}
