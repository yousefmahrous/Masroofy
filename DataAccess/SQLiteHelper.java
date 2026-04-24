package DataAccess;

import java.sql.*;
import model.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class SQLiteHelper implements IDatabase {
    private static final String dbName = "Masrofy.db";
    private static final String url = "jdbc:sqlite:" + dbName;

    private Connection connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver not found in Classpath!");
        }
        return DriverManager.getConnection(url);
    }
    
    @Override
    public void onCreate() {
        String budgetTable = "CREATE TABLE IF NOT EXISTS budget_cycle (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, total_allowance REAL, current_spent REAL, " +
                "start_date TEXT, end_date TEXT, daily_limit REAL)";
        String categoryTable = "CREATE TABLE IF NOT EXISTS categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, budget_limit REAL, current_spent REAL)";
        String transactionTable = "CREATE TABLE IF NOT EXISTS transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, category_id INTEGER, budget_id INTEGER, " +
                "amount REAL, notes TEXT, time TEXT, type TEXT)";
        
        try (Connection conn = this.connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(budgetTable);
            stmt.execute(categoryTable);
            stmt.execute(transactionTable);
            System.out.println("Database was created successfully.");
        } catch (SQLException e) {
            System.err.println("Create Error: " + e.getMessage());
        }
    }
   
    @Override
    public boolean insert(String table, Object data) {
        String sql = "";
        if (data instanceof Category) {
            sql = "INSERT INTO " + table + " (name, budget_limit, current_spent) VALUES(?,?,?)";
        } else if (data instanceof Transaction) {
            sql = "INSERT INTO " + table + " (category_id, budget_id, amount, notes, time, type) VALUES(?,?,?,?,?,?)";
        } else if (data instanceof BudgetCycle) {
            sql = "INSERT INTO " + table + " (total_allowance, current_spent, start_date, end_date, daily_limit) VALUES(?,?,?,?,?)";
        }

        if (sql.isEmpty()) return false;

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (data instanceof Category) {
                Category c = (Category) data;
                pstmt.setString(1, c.getName());
                pstmt.setDouble(2, c.getBudgetLimit());
                pstmt.setDouble(3, c.getCurrentSpent());
            } else if (data instanceof Transaction) {
                Transaction t = (Transaction) data;
                pstmt.setInt(1, t.getCategoryId());
                pstmt.setInt(2, t.getBudgetCycleId());
                pstmt.setDouble(3, t.getAmount());
                pstmt.setString(4, t.getNotes());
                pstmt.setString(5, t.getTime().toString());
                pstmt.setString(6, t.getType().name());
            } else if (data instanceof BudgetCycle) {
                BudgetCycle b = (BudgetCycle) data;
                pstmt.setDouble(1, b.getTotalAllowance());
                pstmt.setDouble(2, b.getCurrentSpent());
                pstmt.setString(3, b.getStartDate().toString());
                pstmt.setString(4, b.getEndDate().toString());
                pstmt.setDouble(5, b.getDailyLimit());
            }
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Insert Error: " + e.getMessage());
            return false;
        }
    }
   
    @Override
    public boolean update(String table, int id, Object data) {
        String sql = "";
        if (data instanceof Category) {
            sql = "UPDATE " + table + " SET name = ?, budget_limit = ?, current_spent = ? WHERE id = ?";
        } else if (data instanceof Transaction) {
            sql = "UPDATE " + table + " SET category_id = ?, budget_id = ?, amount = ?, notes = ?, time = ?, type = ? WHERE id = ?";
        } else if (data instanceof BudgetCycle) {
            sql = "UPDATE " + table + " SET total_allowance = ?, current_spent = ?, start_date = ?, end_date = ?, daily_limit = ? WHERE id = ?";
        }
         
        if (sql.isEmpty()) return false;
        
        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (data instanceof Category) {
                Category c = (Category) data;
                pstmt.setString(1, c.getName());
                pstmt.setDouble(2, c.getBudgetLimit());
                pstmt.setDouble(3, c.getCurrentSpent());
                pstmt.setInt(4, id);
            } else if (data instanceof Transaction) {
                Transaction t = (Transaction) data;
                pstmt.setInt(1, t.getCategoryId());
                pstmt.setInt(2, t.getBudgetCycleId());
                pstmt.setDouble(3, t.getAmount());
                pstmt.setString(4, t.getNotes());
                pstmt.setString(5, t.getTime().toString());
                pstmt.setString(6, t.getType().name());
                pstmt.setInt(7, id);
            } else if (data instanceof BudgetCycle) {
                BudgetCycle b = (BudgetCycle) data;
                pstmt.setDouble(1, b.getTotalAllowance());
                pstmt.setDouble(2, b.getCurrentSpent());
                pstmt.setString(3, b.getStartDate().toString());
                pstmt.setString(4, b.getEndDate().toString());
                pstmt.setDouble(5, b.getDailyLimit());
                pstmt.setInt(6, id);
            }
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update Error: " + e.getMessage());
            return false;
        }
    }
        
    @Override
    public boolean delete(String table, int id) {
        String sql = "DELETE FROM " + table + " WHERE id = ?";
        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete Error: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public List<Object> query(String sql) {
        List<Object> results = new ArrayList<>();
        String sqlLower = sql.toLowerCase().trim();
        if (sqlLower.contains("from transactions") && !sqlLower.contains("join")) {
            sql = "SELECT t.*, c.name AS cat_name FROM transactions t " +
                  "LEFT JOIN categories c ON t.category_id = c.id";
        }

        try (Connection conn = this.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                if (sql.contains("cat_name")) {
                    results.add(new Transaction(
                        rs.getInt("id"),
                        rs.getInt("category_id"),
                        rs.getInt("budget_id"),
                        rs.getDouble("amount"),
                        rs.getString("notes"),
                        LocalDateTime.parse(rs.getString("time")),
                        new Category(rs.getInt("category_id"), rs.getString("cat_name"), 0, 0),
                        TransactionType.valueOf(rs.getString("type"))
                    ));
                } else if (sqlLower.contains("from categories")) {
                    results.add(new Category(rs.getInt("id"), rs.getString("name"), rs.getDouble("budget_limit"), rs.getDouble("current_spent")));
                } else if (sqlLower.contains("from budget_cycle")) {
                    results.add(new BudgetCycle(
                        rs.getInt("id"), rs.getDouble("total_allowance"), rs.getDouble("current_spent"),
                        LocalDate.parse(rs.getString("start_date")), LocalDate.parse(rs.getString("end_date")), rs.getDouble("daily_limit")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Query Error: " + e.getMessage());
        }
        return results;
    }
}