package DB;

/**
 * Created by arthur on 19/05/2015.
 */

//A classe que representa a Tabela do banco de dados, nela teremos qual
//o nome da tabela e dos campos para fazer as consultas sql;

public class Score {
    public static final String TABLE_NAME = "recorde";
    //O android ja reconhece "_id" como chave primaria
    public static final String KEY_ID = "_id";
    public static final String KEY_RECORDE = "recorde";
}
