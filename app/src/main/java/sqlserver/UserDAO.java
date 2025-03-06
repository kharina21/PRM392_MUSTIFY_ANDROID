package sqlserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.User;

public class UserDAO extends DBContext {
    public User getExistUser(String username, String password) {
        String sql = "SELECT [user_id]\n" +
                "      ,[username]\n" +
                "      ,[password]\n" +
                "      ,[first_name]\n" +
                "      ,[last_name]\n" +
                "      ,[gender]\n" +
                "      ,[phone]\n" +
                "      ,[email]\n" +
                "      ,[address]\n" +
                "      ,[join_date]\n" +
                "      ,[account_type]\n" +
                "      ,[is_active]\n" +
                "  FROM [dbo].[User]\n" +
                "  where username = ? and password = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, username);
            st.setString(2, password);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                User a = new User();
                a.setId(rs.getInt("User_id"));
                a.setUsername(rs.getString("username"));
                a.setPassword(rs.getString("password"));
                a.setFirst_name(rs.getString("first_name"));
                a.setLast_name(rs.getString("last_name"));
                a.setGender(rs.getBoolean("gender"));
                a.setPhone(rs.getString("phone"));
                a.setEmail(rs.getString("email"));
                a.setAddress(rs.getString("address"));
                a.setJoined_date(rs.getDate("join_date"));
                a.setAccount_type(rs.getInt("account_type"));
                a.setIs_active(rs.getBoolean("is_active"));
                return a;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public User getUserByUsernameOrEmail(String usernameOrEmail) {
        String sql = "SELECT [User_id]\n" +
                "      ,[username]\n" +
                "      ,[password]\n" +
                "      ,[first_name]\n" +
                "      ,[last_name]\n" +
                "      ,[gender]\n" +
                "      ,[phone]\n" +
                "      ,[email]\n" +
                "      ,[address]\n" +
                "      ,[join_date]\n" +
                "      ,[account_type]\n" +
                "      ,[is_active]\n" +
                "  FROM [dbo].[User]\n" +
                "   where username = ? or email = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, usernameOrEmail);
            st.setString(2, usernameOrEmail);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                User a = new User();
                a.setId(rs.getInt("User_id"));
                a.setUsername(rs.getString("username"));
                a.setPassword(rs.getString("password"));
                a.setFirst_name(rs.getString("first_name"));
                a.setLast_name(rs.getString("last_name"));
                a.setGender(rs.getBoolean("gender"));
                a.setPhone(rs.getString("phone"));
                a.setEmail(rs.getString("email"));
                a.setAddress(rs.getString("address"));
                a.setJoined_date(rs.getDate("join_date"));
                a.setAccount_type(rs.getInt("account_type"));
                a.setIs_active(rs.getBoolean("is_active"));
                return a;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean changePasswordByUsername(String username, String password) {
        String sql = "UPDATE [dbo].[User]\n" +
                "   SET [password] = ?\n" +
                " WHERE username = ?";
        try{
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, password);
            st.setString(2, username);
            st.executeUpdate();
            return true;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkEmailExist(String email) {
        String sql = "SELECT  [User_id]\n" +
                "      ,[username]\n" +
                "      ,[password]\n" +
                "      ,[first_name]\n" +
                "      ,[last_name]\n" +
                "      ,[gender]\n" +
                "      ,[phone]\n" +
                "      ,[email]\n" +
                "      ,[address]\n" +
                "      ,[join_date]\n" +
                "      ,[account_type]\n" +
                "      ,[is_active]\n" +
                "  FROM [MUSTIFY].[dbo].[User]\n" +
                "  where email = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, email);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkUsernameExist(String username) {
        String sql = "SELECT  [User_id]\n" +
                "      ,[username]\n" +
                "      ,[password]\n" +
                "      ,[first_name]\n" +
                "      ,[last_name]\n" +
                "      ,[gender]\n" +
                "      ,[phone]\n" +
                "      ,[email]\n" +
                "      ,[address]\n" +
                "      ,[join_date]\n" +
                "      ,[account_type]\n" +
                "      ,[is_active]\n" +
                "  FROM [MUSTIFY].[dbo].[User]\n" +
                "  where username = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, username);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean addUser(User a) {
        String sql = "INSERT INTO [dbo].[User]\n" +
                "           ([username]\n" +
                "           ,[password]\n" +
                "           ,[first_name]\n" +
                "           ,[last_name]\n" +
                "           ,[gender]\n" +
                "           ,[email]\n" +
                "           ,[account_type])\n" +
                "     VALUES\n" +
                "           (?,?,?,?,?,?,?)";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, a.getUsername());
            st.setString(2, a.getPassword());
            st.setString(3, a.getFirst_name());
            st.setString(4, a.getLast_name());
            st.setBoolean(5, a.isGender());
            st.setString(6, a.getEmail());
            st.setInt(7, a.getAccount_type());
            st.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean changeActiveStatusByUserId(String username, boolean active) {
        String sql = "\n" +
                "UPDATE [dbo].[User]\n" +
                "   SET [is_active] = ?\n" +
                " WHERE username = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setBoolean(1, active);
            st.setString(2, username);
            st.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
