package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        VideoEntity::class,
        ChannelEntity::class,
        CommentEntity::class,
        NotificationEntity::class,
        LiveChatMessageEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class VidioDatabase : RoomDatabase() {
    abstract fun vidioDao(): VidioDao

    companion object {
        @Volatile
        private var INSTANCE: VidioDatabase? = null

        fun getDatabase(context: Context): VidioDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VidioDatabase::class.java,
                    "vidiotube_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
