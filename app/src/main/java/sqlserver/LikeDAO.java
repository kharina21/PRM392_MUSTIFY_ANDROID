package sqlserver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Like;
import model.Song;
import model.User;

public class LikeDAO extends DBContext {
    public List<Like> getListSongLikeByUserId(int id) {
        List<Like> list = new ArrayList<>();
        String sql = "\n" +
                "SELECT L.like_id, L.user_id, L.song_id, L.created_date, S.title, S.artist, S.album, S.song_id, S.type_id,S.duration,S.file_path,S.image,S.created_date\n" +
                "FROM Likes L\n" +
                "JOIN Song S ON L.song_id = S.song_id\n" +
                "WHERE L.user_id = ?\n" +
                "ORDER BY L.created_date DESC;\n";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Like l = new Like();
                l.setLike_id(rs.getInt("like_id"));

                UserDAO udb = new UserDAO();
                User u = udb.getUserById(rs.getInt("user_id"));
                l.setUser(u);

                SongDAO sdb = new SongDAO();
                Song s = sdb.getSongById(rs.getInt("song_id"));
                l.setSong(s);

                l.setCreated_date(rs.getDate("created_date"));

                list.add(l);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addSongToListLike(int uId, int sId){
        String sql = "INSERT INTO [dbo].[Likes]\n" +
                "           ([user_id]\n" +
                "           ,[song_id])\n" +
                "     VALUES(?,?)";
        try{
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1,uId);
            st.setInt(2,sId);
            st.executeUpdate();
            return true;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeSongInListLike(int uId, int sId){
        String sql = "DELETE FROM [dbo].[Likes]\n" +
                "      WHERE user_id = ? and song_id = ?";
        try{
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1,uId);
            st.setInt(2,sId);
            st.executeUpdate();
            return true;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
