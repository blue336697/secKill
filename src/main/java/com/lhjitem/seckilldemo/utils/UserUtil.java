package com.lhjitem.seckilldemo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lhjitem.seckilldemo.domain.CommonBean;
import com.lhjitem.seckilldemo.pojo.User;
import org.apache.ibatis.javassist.bytecode.ByteArray;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 专门生成很多个用户用作压力测试
 * @author lhj
 * @create 2022/3/7 17:55
 */
public class UserUtil {
    public static final int COUNT = 2;
    private static void createUser() throws Exception {
        //准备5000个生成的不同user对象
        List<User> users = new ArrayList<>(COUNT);
        for (int i = 0; i < COUNT; i++) {
            User user = new User();
            user.setId(13195781130L+i);
            user.setNickname("user"+i);
            user.setSalt("l1h2j3");
            user.setPassword(MD5Util.inputPassToDBPass("200627", user.getSalt()));
            user.setLoginCount(1);
            user.setRegisterDate(new Date());
            users.add(user);
        }
        System.out.println("add user to userList is done!");




        //接下来插入到数据库中
        Connection connection = getConn();
        String sql = "insert into t_user(id,nickname,password,salt,register_date,login_count) values(?,?,?,?,?,?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            statement.setLong(1, user.getId());
            statement.setString(2, user.getNickname());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getSalt());
            statement.setTimestamp(5, new Timestamp(user.getRegisterDate().getTime()));
            statement.setInt(6, user.getLoginCount());
            statement.addBatch();
        }
        statement.executeBatch();
        statement.close();
        connection.close();
        System.out.println("insert to db is done!");


        //现在获取session中的cookie，即我们的user_ticket
        String urlString = "http://localhost:8080/login/toLogin";
        File file = new File("C:\\Users\\hp\\Desktop\\config.txt");
        if (file.exists())
            file.delete();
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.seek(0);
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection) url.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            OutputStream os = co.getOutputStream();
            String params = "mobile=" + user.getId() + "&password=" + MD5Util.inputPassToFromPass("200627");
            os.write(params.getBytes());
            os.flush();
            InputStream is = co.getInputStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = is.read(buff)) >= 0){
                bos.write(buff, 0, len);
            }
            is.close();
            bos.close();

            String res = new String(bos.toByteArray());
            ObjectMapper objectMapper = new ObjectMapper();
            CommonBean commonBean = objectMapper.readValue(res, CommonBean.class);
            String userTicket = (String) commonBean.getObj();
            System.out.println("令牌为："+userTicket);



            //现在根据获取到userTicket写入到txt文件中，通过流的形式
            String row = user.getId() + "," + userTicket;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to txt is done!,by" + user.getId());
        }
        raf.close();
        System.out.println("over!");
    }

    private static Connection getConn() throws Exception{
        String url = "jdbc:mysql://localhost:3306/seckill_db?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "200627";
        String driver = "com.mysql.cj.jdbc.Driver";
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }

    public static void main(String[] args) throws Exception {
        createUser();
    }
}
