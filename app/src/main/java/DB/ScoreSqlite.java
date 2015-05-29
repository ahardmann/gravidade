package DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by arthur on 19/05/2015.
 */

//Essa classe representa o banco de dados, ela deve extender de
//SQLiteOpenHelper e é nela que montaremos toda a estrutura do banco de dados

public class ScoreSqlite extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "recorde";
    public static final int DATABASE_VERSION = 1;

    public ScoreSqlite (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase arg0) {
        String CREATE_TABLE_RECORD = "CREATE TABLE "+ Score.TABLE_NAME +
                " ("+Score.KEY_ID + " integer primary key, " +
                Score.KEY_RECORDE + " long); ";
        arg0.execSQL(CREATE_TABLE_RECORD);

        String INICIALIZA_TABELA_RECORD = "INSERT INTO "+Score.TABLE_NAME +
                "("+Score.KEY_ID+","+Score.KEY_RECORDE+")VALUES(1,0);";

        arg0.execSQL(INICIALIZA_TABELA_RECORD);

    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }
}

