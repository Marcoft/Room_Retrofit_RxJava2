package project.study.room.Room;

import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {User.class},version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDAO userDAO();

}
