package project.study.room.Room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface UserDAO {

    @Query("Select * FROM user_table")
    Flowable<List<User>> getAllUsers();

    @Insert
    Completable insertUser(User...users);

    @Query("Select * From user_table Where id LIKE :uid")
    Single<User> findUserById(int uid);

    @Update
    Completable updateUser(User ... users);

    @Delete
    Completable  deleteUser(User ... users);

    @Query("DELETE FROM user_table")
    Completable deleteAll();
}
