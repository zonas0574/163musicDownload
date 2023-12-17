package zonas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class API {

    @SuppressWarnings("unchecked")
    public static String playlist_detail(String id, List<Map<String, Object>> fileList) {
        String url = Util.HOSTURL + "weapi/v3/playlist/detail?csrf_token=";
        String param = "{id:'" + id + "', csrf_token:''}";
        String json = Util.getJson(param, url);
        Map<String, Object> map = Util.getMapByJson(json);
        if (map.get("code").toString().contains("404")) {
            System.out.println("解析歌单失败，id:" + id);
            return "";
        }
        List<Map<String, Object>> playlist = (List<Map<String, Object>>) ((Map<String, Object>) map.get("playlist")).get("trackIds");
        StringBuilder ids = new StringBuilder();
        for (Map<String, Object> play : playlist) {
            ids.append(Util.StringToInt(play.get("id").toString())).append(",");
        }
        return song_detail(ids.toString(), fileList);
    }

    @SuppressWarnings("unchecked")
    static String song_detail(String id, List<Map<String, Object>> fileList) {
        String url = Util.HOSTURL + "weapi/v3/song/detail?csrf_token=";
        String[] ids = id.split(",");
        StringBuilder c = new StringBuilder();
        for (String i : ids) {
            c.append("{\"id\":").append(i).append("},");
        }
        String param = "{ids:'[" + id + "]',c:'[" + c.substring(0, c.length() - 1) + "]', csrf_token:''}";
        String json = Util.getJson(param, url);
        Map<String, Object> map = Util.getMapByJson(json);
        if (map.get("code").toString().contains("404")) {
            System.out.println("解析歌曲详情失败，id:" + id);
            return "";
        }
        List<Map<String, Object>> songList = (List<Map<String, Object>>) map.get("songs");
        return listToIds(songList, fileList);
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> song_url(String ids, List<Map<String, Object>> fileList) {
        String url = Util.HOSTURL + "weapi/song/enhance/player/url?csrf_token=";
        String param = "{ids:'[" + ids + "]',br:999000, csrf_token:''}";
        String json = Util.getJson(param, url);
        Map<String, Object> map = Util.getMapByJson(json);
        List<Map<String, Object>> songs = (List<Map<String, Object>>) map.get("data");
        for (Map<String, Object> song : songs) {
            for (Map<String, Object> aSongList : fileList) {
                if (Util.StringToInt(song.get("id").toString()) == Util.StringToInt(aSongList.get("id").toString())) {
                    aSongList.put("url", song.get("url"));
                    aSongList.put("type", song.get("type"));
                }
            }

        }
        return fileList;
    }

    @SuppressWarnings("unchecked")
    private static String listToIds(List<Map<String, Object>> inList, List<Map<String, Object>> outList) {
        StringBuilder ids = new StringBuilder();
        Map<String, Object> songs;
        for (Map<String, Object> map : inList) {
            songs = new HashMap<String, Object>();
            ids.append(Util.StringToInt(map.get("id").toString())).append(",");

            // 获取歌手信息
            List<Map<String, Object>> ar = (List<Map<String, Object>>) map.get("ar");
            StringBuilder singers = new StringBuilder();
            for (Map<String, Object> map1 : ar) {
                singers.append(map1.get("name").toString()).append(",");
            }

            // 获取专辑信息
            Map<String, Object> al = (Map<String, Object>) map.get("al");
            String album = al.get("name").toString();
            String pic = null;  // picUrl

            singers.deleteCharAt(singers.length() - 1);
            songs.put("id", Util.StringToInt(map.get("id").toString()));
            songs.put("name", map.get("name"));
            songs.put("singer", singers);
            songs.put("album", album);
            songs.put("filename", songs.get("name") + " - " + songs.get("singer"));  // 曲名在前歌手在后
            outList.add(songs);
        }
        if (inList.isEmpty()) {
            return ids.toString();
        } else {
            return ids.substring(0, ids.length() - 1);
        }

    }
}