package com.example.musicapplicationtemplate.sqlserver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.musicapplicationtemplate.model.Song;
import com.example.musicapplicationtemplate.model.User;
import com.example.musicapplicationtemplate.model.RecentlyPlayed;

public class RecentlyPlayedDAO extends DBContext {
    public List<RecentlyPlayed> get10SongsRecentlyPlayedByUserId(int UserId) {
        List<RecentlyPlayed> list = new ArrayList<>();
        String sql = "SELECT TOP 10 rp.[id], \n" +
                "       rp.[user_id], \n" +
                "       rp.[song_id], \n" +
                "       rp.[played_at], \n" +
                "       s.[title], \n" +
                "       s.[artist], \n" +
                "       s.[album], \n" +
                "       s.[duration], \n" +
                "       s.[image], \n" +
                "       s.[file_path], \n" +
                "       s.[created_date]\n" +
                "FROM [dbo].[Recently_played] rp\n" +
                "JOIN [dbo].[Song] s ON rp.[song_id] = s.[song_id]\n" +
                "WHERE rp.[user_id] = ?\n" +
                "AND rp.[played_at] = (\n" +
                "    SELECT MAX(played_at)\n" +
                "    FROM [dbo].[Recently_played] rp2\n" +
                "    WHERE rp2.song_id = rp.song_id\n" +
                ")\n" +
                "ORDER BY rp.[played_at] DESC;";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, UserId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                RecentlyPlayed rp = new RecentlyPlayed();
                rp.setId(rs.getInt("id"));

                UserDAO udb = new UserDAO();
                User u = udb.getUserById(rs.getInt("user_id"));
                rp.setUser(u);

                SongDAO sdb = new SongDAO();
                Song s = sdb.getSongById(rs.getInt("song_id"));
                rp.setSong(s);

                rp.setPlayed_at(rs.getDate("played_at"));
                list.add(rp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addSongPlayed(User u, Song s) {
        String sql = "INSERT INTO [dbo].[Recently_played]\n" +
                "           ([user_id]\n" +
                "           ,[song_id])\n" +
                "     VALUES\n" +
                "           (?,?)";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, u.getId());
            st.setInt(2, s.getSong_id());
            st.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
