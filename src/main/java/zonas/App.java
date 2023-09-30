package zonas;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class App {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Scanner sc = new Scanner(System.in);
        System.out.print("请选择下载类型（1.歌单id 2.歌曲id）：");
        String temp = sc.nextLine();
        List<Map<String, Object>> playlist = new ArrayList<Map<String, Object>>();
        String ids;
        if ("1".equals(temp)) {
            System.out.print("请输入歌单id（仅限单id）：");
            String playId = sc.nextLine();
            ids = API.playlist_detail(playId, playlist);
        } else if ("2".equals(temp)) {
            System.out.print("请输入歌曲id（支持多个id用','隔开）：");
            String songIds = sc.nextLine();
            ids = API.song_detail(songIds, playlist);
        } else {
            System.out.println("请输入正确类型");
            return;
        }
        playlist = API.song_url(ids, playlist);
        System.out.println("解析完成，共下载 " + playlist.size() + " 首歌曲");
        if (!Util.createDir("music163")) {
            System.out.println("创建文件夹失败，退出下载");
            return;
        }
        for (Map<String, Object> map : playlist) {
            String name = map.get("name").toString();
            String type = map.get("type").toString();
            String filename = "%s.%s".formatted(name, type);
            if (map.get("url") != null) {
                try {
                    byte[] data = Util.fileDownload(map.get("url").toString());
                    FileOutputStream outputStream = new FileOutputStream("music163" + File.separator + Util.formatFilePath(filename));
                    outputStream.write(data);
                    System.out.println("已下载歌曲：" + Util.formatFilePath(filename));
                    outputStream.close();
                } catch (IOException e) {
                    System.out.println("无法创建歌曲文件：" + filename);
                }
            } else {
                System.out.println("无法获取下载地址：" + filename);
            }
        }
        System.exit(0);
    }
}
