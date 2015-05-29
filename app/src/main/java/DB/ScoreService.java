package DB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by arthur on 19/05/2015.
 */

//Essa classe que efetua as consultas, insert, updade e delete.
//No nosso exemplo utilizaremos apenas 2 metodos, um para selecionar o record atual e outro para
//alterar o record atual(update).

public class ScoreService {
    private ScoreSqlite scoreSqlite;
    private SQLiteDatabase database;

    public ScoreService(ScoreSqlite pscoreSqlite) {
        scoreSqlite = pscoreSqlite;
    }
    public void open(){
        database = scoreSqlite.getWritableDatabase();
    }

    public Long getRecorde() {
        Cursor cursor;
        cursor = database.query(Score.TABLE_NAME,
                new String[]{Score.KEY_ID+",MAX("+Score.KEY_RECORDE+") as "+Score.KEY_RECORDE},
                null, null,
                null, null, null);
        cursor.moveToFirst();
        Long retorno = cursor.getLong(cursor.getColumnIndex(Score.KEY_RECORDE));
        cursor.close();
        return retorno;
    }

    public void novoRecorde(Long record){
        ContentValues values = new ContentValues();
        values.put(Score.KEY_RECORDE, record);
        database.update(Score.TABLE_NAME, values, "_id = ?", new String[]{Long.toString(1)});
    }

}

