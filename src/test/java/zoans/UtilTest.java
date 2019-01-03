package zoans;

import zonas.API;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 工具类测试
 *
 * @author zonas
 * @since 2019-01-03
 */
public class UtilTest {
    public static void main(String[] args) {
        List<Map<String, Object>> playlist = new ArrayList<Map<String, Object>>();
        String ids = API.playlist_detail("2290826509", playlist);
        playlist = API.song_url(ids, playlist);
        System.out.println(playlist);
    }
}
