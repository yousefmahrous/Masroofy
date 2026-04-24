package DataAccess;

import java.util.List;

public interface IDatabase {
   void onCreate();
   boolean insert(String table, Object data);
   boolean update(String table, int id, Object data);
   boolean delete(String table, int id);
   List<Object> query(String sql);
}