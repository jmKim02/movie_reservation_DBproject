package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MainLoginView extends JFrame {
	// 메인 로그인 화면 생성
    public MainLoginView() {
    	 setTitle("영화 예매 프로그램");
         setSize(640, 480);
         setLocationRelativeTo(null);
         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

         // 메인 패널 생성 및 설정
         JPanel mainPanel = new JPanel();
         mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
         mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
         
         // 타이틀 레이블 생성 및 설정
         JLabel titleLabel = new JLabel("영화 예매 프로그램", SwingConstants.CENTER);
         titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
         titleLabel.setFont(new Font("Serif", Font.BOLD, 30));
         titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0)); 
         mainPanel.add(titleLabel);

         // 버튼 패널 생성 및 설정
         JPanel buttonPanel = new JPanel();
         buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
         
         // 관리자 버튼 생성 및 설정
         JButton adminButton = new JButton("관리자");
         adminButton.setPreferredSize(new Dimension(200, 200));
         adminButton.setFont(new Font("Serif", Font.PLAIN, 18));
         adminButton.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 try {
                     Class.forName("com.mysql.cj.jdbc.Driver");
                     Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db1", "root", "1234");
                     new AdminPage(conn).setVisible(true); // DB 연결에 성공하면 관리자 페이지로 이동
                     dispose();
                 } catch (ClassNotFoundException ex) {
                     ex.printStackTrace();
                     JOptionPane.showMessageDialog(null, "JDBC 드라이버 로드 오류. 관리자 로그인 실패!");
                 } catch (SQLException ex) {
                     ex.printStackTrace();
                     JOptionPane.showMessageDialog(null, "DB 연결 오류. 관리자 로그인 실패!");
                 }
             }
         });
         buttonPanel.add(adminButton);

         // 회원 버튼 생성 및 설정
         JButton memberButton = new JButton("회원");
         memberButton.setPreferredSize(new Dimension(200, 200));
         memberButton.setFont(new Font("Serif", Font.PLAIN, 18));
         memberButton.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 try {
                     Class.forName("com.mysql.cj.jdbc.Driver");
                     Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db1", "user1", "user1");
                     new MemberPage(conn).setVisible(true); // DB 연결에 성공하면 회원 페이지로 이동
                     dispose(); 
                 } catch (ClassNotFoundException ex) {
                     ex.printStackTrace();
                     JOptionPane.showMessageDialog(null, "JDBC 드라이버 로드 오류.");
                 } catch (SQLException ex) {
                     ex.printStackTrace();
                     JOptionPane.showMessageDialog(null, "DB 연결 오류. 회원 로그인 실패!");
                 }
             }
         });
         buttonPanel.add(memberButton);
         
         // 버튼 패널을 메인 패널에 추가
         buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
         mainPanel.add(buttonPanel);
         
         // 메인 패널을 프레임에 추가하고 프레임 표시
         add(mainPanel);
         setVisible(true);
    }
}