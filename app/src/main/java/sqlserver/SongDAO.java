package sqlserver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Song;

public class SongDAO extends DBContext{
    public List<Song> getAllSongs(){
        List<Song> list = new ArrayList<>();
        String sql = "SELECT [song_id]\n" +
                "      ,[title]\n" +
                "      ,[type_id]\n" +
                "      ,[artist]\n" +
                "      ,[album]\n" +
                "      ,[duration]\n" +
                "      ,[file_path]\n" +
                "      ,[created_date]\n" +
                "      ,[image]\n" +
                "  FROM [dbo].[Song]";
        try{
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                Song s = new Song();
                s.setSong_id(rs.getInt("song_id"));
                s.setTitle(rs.getString("title"));
                s.setType_id(rs.getInt("type_id"));
                s.setArtist(rs.getString("artist"));
                s.setAlbum(rs.getString("album"));
                s.setDuration(rs.getInt("duration"));
                s.setFile_path(rs.getString("file_path"));
                s.setCreated_date(rs.getDate("created_date"));
                s.setImage(rs.getString("image"));
                list.add(s);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return list;
    }
}
