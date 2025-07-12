package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class AdminPage extends JFrame {
    private Connection conn;
    private JPanel resultPanel;

    public AdminPage(Connection conn) {
        this.conn = conn;
        setTitle("관리자 페이지");
        setSize(960, 640);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 각 기능 버튼 생성
        JButton resetButton = new JButton("데이터베이스 초기화");
        JButton viewTablesButton = new JButton("전체 테이블 보기");
        JButton deleteButton = new JButton("데이터 삭제");
        JButton updateButton = new JButton("데이터 변경");
        JButton insertButton = new JButton("데이터 입력");

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetDatabase();
            }
        });

        viewTablesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAllTables();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDeleteDialog();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showUpdateDialog();
            }
        });

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showInsertDialog();
            }
        });

        // 기본 화면 레이아웃 설정
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(resetButton);
        buttonPanel.add(viewTablesButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(insertButton);

        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(resultPanel);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(buttonPanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }
    
    // 데이터베이스 초기화 기능
    private void resetDatabase() {
        try (Statement stmt = conn.createStatement()) {
            // 초기 조건 체크
            stmt.executeUpdate("SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;");
            stmt.executeUpdate("SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;");
            stmt.executeUpdate("SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';");

            // 데이터베이스 db1 사용
            stmt.executeUpdate("USE `db1`;");

            // 테이블 생성
            executeUpdate(stmt, "drop table if exists members;");
            executeUpdate(stmt, "create table members (" +
                "  `member_id` VARCHAR(45) NOT NULL COLLATE utf8_general_ci," +
                "  `member_name` VARCHAR(20) NOT NULL," +
                "  `phone_number` VARCHAR(20) NOT NULL," +
                "  `email` VARCHAR(45) NOT NULL," +
                "  PRIMARY KEY (`member_id`)," +
                "  UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE," +
                "  UNIQUE INDEX `phone_number_UNIQUE` (`phone_number` ASC) VISIBLE" +
                ");");

            executeUpdate(stmt, "drop table if exists movies;");
            executeUpdate(stmt, "create table movies (" +
                "  `movie_number` INT NOT NULL AUTO_INCREMENT," +
                "  `movie_title` VARCHAR(45) NOT NULL," +
                "  `running_time` TIME NOT NULL," +
                "  `screening_rating` VARCHAR(20) NOT NULL," +
                "  `director` VARCHAR(45) NOT NULL," +
                "  `actor` VARCHAR(45) NOT NULL," +
                "  `genre` VARCHAR(45) NOT NULL," +
                "  `movie_introduction` VARCHAR(2000) NOT NULL," +
                "  `release_date` DATE NOT NULL," +
                "  `movie_rating` DECIMAL(4,2) NOT NULL," +
                "  PRIMARY KEY (`movie_number`)" +
                ");");

            executeUpdate(stmt, "drop table if exists reservation;");
            executeUpdate(stmt, "create table reservation (" +
                "  `reservation_number` INT NOT NULL AUTO_INCREMENT," +
                "  `member_id` VARCHAR(45) NOT NULL COLLATE utf8_general_ci," +
                "  `payment_method` VARCHAR(45) NOT NULL," +
                "  `payment_status` VARCHAR(10) NOT NULL," +
                "  `payment_amount` INT NOT NULL," +
                "  `payment_date` DATE NOT NULL," +
                "  PRIMARY KEY (`reservation_number`)," +
                "  INDEX `fk_Reservation_Members1_idx` (`member_id` ASC) VISIBLE," +
                "  CONSTRAINT `fk_Reservation_Members1`" +
                "    FOREIGN KEY (`member_id`)" +
                "    REFERENCES `db1`.`members` (`member_id`)" +
                "    ON DELETE CASCADE" +
                "    ON UPDATE CASCADE" +
                ");");

            executeUpdate(stmt, "drop table if exists theater;");
            executeUpdate(stmt, "create table theater (" +
                "  `theater_number` INT NOT NULL AUTO_INCREMENT," +
                "  `theater_capacity` INT NOT NULL," +
                "  `theater_status` VARCHAR(10) NOT NULL," +
                "  PRIMARY KEY (`theater_number`)" +
                ");");

            executeUpdate(stmt, "drop table if exists schedules;");
            executeUpdate(stmt, "create table schedules (" +
                "  `schedule_number` INT NOT NULL AUTO_INCREMENT," +
                "  `movie_number` INT NOT NULL," +
                "  `theater_number` INT NOT NULL," +
                "  `screening_start_date` DATE NOT NULL," +
                "  `screening_day` VARCHAR(45) NOT NULL," +
                "  `screening_round` INT NOT NULL," +
                "  `screening_start_time` TIME NOT NULL," +
                "  PRIMARY KEY (`schedule_number`)," +
                "  INDEX `fk_Schedules_Theater1_idx` (`theater_number` ASC) VISIBLE," +
                "  INDEX `fk_Schedules_Movies1_idx` (`movie_number` ASC) VISIBLE," +
                "  CONSTRAINT `fk_Schedules_Movies1`" +
                "    FOREIGN KEY (`movie_number`)" +
                "    REFERENCES `db1`.`movies` (`movie_number`)" +
                "    ON DELETE CASCADE" +
                "    ON UPDATE CASCADE," +
                "  CONSTRAINT `fk_Schedules_Theater1`" +
                "    FOREIGN KEY (`theater_number`)" +
                "    REFERENCES `db1`.`theater` (`theater_number`)" +
                "    ON DELETE CASCADE" +
                "    ON UPDATE CASCADE" +
                ");");

            executeUpdate(stmt, "drop table if exists seat;");
            executeUpdate(stmt, "create table seat (" +
                "  `seat_number` VARCHAR(10) NOT NULL COLLATE utf8_general_ci," +
                "  `theater_number` INT NOT NULL," +
                "  `schedule_number` INT NOT NULL," + 
                "  `seat_status` VARCHAR(10) NOT NULL," +
                "  PRIMARY KEY (`seat_number`, `theater_number`, `schedule_number`)," + 
                "  INDEX `fk_Seat_Theater1_idx` (`theater_number` ASC) VISIBLE," +
                "  INDEX `fk_Seat_ Schedules1_idx` (`schedule_number` ASC) VISIBLE," +
                "  CONSTRAINT `fk_Seat_Theater1`" +
                "    FOREIGN KEY (`theater_number`)" +
                "    REFERENCES `db1`.`theater` (`theater_number`)" +
                "    ON DELETE CASCADE" +
                "    ON UPDATE CASCADE," +
                "  CONSTRAINT `fk_Seat_ Schedules1`" + 
                "    FOREIGN KEY (`schedule_number`)" +
                "    REFERENCES `db1`.`schedules` (`schedule_number`)" +
                "    ON DELETE CASCADE" +
                "    ON UPDATE CASCADE" +
                ");");

            executeUpdate(stmt, "drop table if exists ticket;");
            executeUpdate(stmt, "create table ticket (" +
                "  `ticket_number` INT NOT NULL AUTO_INCREMENT," +
                "  `schedule_number` INT NOT NULL," +
                "  `seat_number` VARCHAR(10) NOT NULL COLLATE utf8_general_ci," +
                "  `theater_number` INT NOT NULL," +
                "  `reservation_number` INT NOT NULL," +
                "  `ticket_status` VARCHAR(10) NOT NULL," +
                "  `standard_price` INT NOT NULL," +
                "  `sale_price` INT NOT NULL," +
                "  PRIMARY KEY (`ticket_number`)," +
                "  INDEX `fk_Ticket_schedules1_idx` (`schedule_number` ASC) VISIBLE," +
                "  INDEX `fk_Ticket_reservation1_idx` (`reservation_number` ASC) VISIBLE," +
                "  INDEX `fk_Ticket_seat1_idx` (`seat_number` ASC, `theater_number` ASC) VISIBLE," +
                "  CONSTRAINT `fk_Ticket_reservation1`" +
                "    FOREIGN KEY (`reservation_number`)" +
                "    REFERENCES `db1`.`reservation` (`reservation_number`)" +
                "    ON DELETE CASCADE" +
                "    ON UPDATE CASCADE," +
                "  CONSTRAINT `fk_Ticket_seat1`" +
                "    FOREIGN KEY (`seat_number`, `theater_number`)" +
                "    REFERENCES `db1`.`seat` (`seat_number`, `theater_number`)" +
                "    ON DELETE CASCADE" +
                "    ON UPDATE CASCADE," +
                "  CONSTRAINT `fk_Ticket_schedules1`" +
                "    FOREIGN KEY (`schedule_number`)" +
                "    REFERENCES `db1`.`schedules` (`schedule_number`)" +
                "    ON DELETE CASCADE" +
                "    ON UPDATE CASCADE" +
                ");");
            
            stmt.executeUpdate("ALTER TABLE Movies AUTO_INCREMENT=1;");
			stmt.executeUpdate("ALTER TABLE Theater AUTO_INCREMENT=1;");
			stmt.executeUpdate("ALTER TABLE Schedules AUTO_INCREMENT=1;");
			stmt.executeUpdate("ALTER TABLE Reservation AUTO_INCREMENT=1;");
			stmt.executeUpdate("ALTER TABLE Ticket AUTO_INCREMENT=1;");
            
            // 기본 회원 정보 추가
            stmt.executeUpdate("INSERT INTO Members VALUES('kim0226', '김종민', '010-1111-1234' ,'kim0226@gmail.com');");
            stmt.executeUpdate("INSERT INTO Members VALUES('park0110', '박하나', '010-1111-1235' ,'park0110@gmail.com');");
            stmt.executeUpdate("INSERT INTO Members VALUES('lee0326', '이지환', '010-1111-1236' ,'lee0326@gmail.com');");
            stmt.executeUpdate("INSERT INTO Members VALUES('lee0120', '이혜원', '010-1111-1237' ,'lee0120@gmail.com');");
            stmt.executeUpdate("INSERT INTO Members VALUES('jung0420', '정성호', '010-1111-1238' ,'jung0420@gmail.com');");
            
            stmt.executeUpdate("INSERT INTO Members VALUES('jung0210', '정준하', '010-2222-1234' ,'jung0110@gmail.com');");
            stmt.executeUpdate("INSERT INTO Members VALUES('park0520', '박명수', '010-2222-1235' ,'park0520@gmail.com');");
            stmt.executeUpdate("INSERT INTO Members VALUES('chul0621', '노홍철', '010-2222-1236' ,'chul0621@gmail.com');");
            stmt.executeUpdate("INSERT INTO Members VALUES('haha0721', '하하', '010-2222-1237' ,'haha0721@gmail.com');");
            stmt.executeUpdate("INSERT INTO Members VALUES('jung0910', '정형돈', '010-2222-1238' ,'jung0910@naver.com');");
            stmt.executeUpdate("INSERT INTO Members VALUES('yoo0111', '유재석', '010-2222-1239' ,'yoo0111@naver.com');");
            
            stmt.executeUpdate("INSERT INTO Members VALUES('kang0121', '강호동', '010-3333-1234' ,'kang0121@naver.com');");
            stmt.executeUpdate("INSERT INTO Members VALUES('lee1121', '이수근', '010-3333-1235' ,'lee1121@naver.com');");
            stmt.executeUpdate("INSERT INTO Members VALUES('lee0810', '이승기', '010-3333-1236' ,'lee0810@naver.com');");
            stmt.executeUpdate("INSERT INTO Members VALUES('one1223', '은지원', '010-3333-1237' ,'one1223@naver.com');");
            stmt.executeUpdate("INSERT INTO Members VALUES('mong1022', 'MC몽', '010-3333-1238' ,'mong1022@naver.com');");
            
            // 기본 티켓 정보 추가
            stmt.executeUpdate("INSERT INTO Ticket VALUES(0, 1, 'A11', 1, 1, 'Booked', 12000, 12000);");
            stmt.executeUpdate("INSERT INTO Ticket VALUES(0, 1, 'A12', 1, 2, 'Booked', 12000, 12000);");
            stmt.executeUpdate("INSERT INTO Ticket VALUES(0, 1, 'A13', 1, 3, 'Booked', 12000, 12000);");
            stmt.executeUpdate("INSERT INTO Ticket VALUES(0, 1, 'A14', 1, 4, 'Booked', 12000, 12000);");
            stmt.executeUpdate("INSERT INTO Ticket VALUES(0, 1, 'A15', 1, 5, 'Booked', 12000, 12000);");
            stmt.executeUpdate("INSERT INTO Ticket VALUES(0, 1, 'A16', 1, 6, 'Booked', 12000, 12000);");
            stmt.executeUpdate("INSERT INTO Ticket VALUES(0, 1, 'A17', 1, 7, 'Booked', 12000, 12000);");
            stmt.executeUpdate("INSERT INTO Ticket VALUES(0, 1, 'A18', 1, 8, 'Booked', 12000, 12000);");
            stmt.executeUpdate("INSERT INTO Ticket VALUES(0, 1, 'A19', 1, 9, 'Booked', 12000, 12000);");
            stmt.executeUpdate("INSERT INTO Ticket VALUES(0, 1, 'A20', 1, 10, 'Booked', 12000, 12000);");
            stmt.executeUpdate("INSERT INTO Ticket VALUES(0, 1, 'A21', 1, 11, 'Booked', 12000, 12000);");
            stmt.executeUpdate("INSERT INTO Ticket VALUES(0, 1, 'A22', 1, 12, 'Booked', 12000, 12000);");
            stmt.executeUpdate("INSERT INTO Ticket VALUES(0, 1, 'A23', 1, 13, 'Booked', 12000, 12000);");
            stmt.executeUpdate("INSERT INTO Ticket VALUES(0, 1, 'A24', 1, 14, 'Booked', 12000, 12000);");
            stmt.executeUpdate("INSERT INTO Ticket VALUES(0, 1, 'A25', 1, 15, 'Booked', 12000, 12000);");
            stmt.executeUpdate("INSERT INTO Ticket VALUES(0, 13, 'A11', 5, 16, 'Booked', 12000, 12000);");
            stmt.executeUpdate("INSERT INTO Ticket VALUES(0, 13, 'A12', 5, 16, 'Booked', 12000, 12000);");
            stmt.executeUpdate("INSERT INTO Ticket VALUES(0, 34, 'A21', 12, 17, 'Booked', 12000, 12000);");
            stmt.executeUpdate("INSERT INTO Ticket VALUES(0, 34, 'A22', 12, 17, 'Booked', 12000, 12000);");
            stmt.executeUpdate("INSERT INTO Ticket VALUES(0, 34, 'A23', 12, 17, 'Booked', 12000, 12000);");
            
            // 기본 예매 정보 추가
            stmt.executeUpdate("INSERT INTO Reservation VALUES(0, 'kim0226', '신용카드', 'paid', 12000, '2024-05-26');");
            stmt.executeUpdate("INSERT INTO Reservation VALUES(0, 'park0110', '신용카드', 'paid', 12000, '2024-05-13');");
            stmt.executeUpdate("INSERT INTO Reservation VALUES(0, 'lee0326', '신용카드', 'paid', 12000, '2024-04-27');");
            stmt.executeUpdate("INSERT INTO Reservation VALUES(0, 'lee0120', '신용카드', 'paid', 12000, '2024-06-04');");
            stmt.executeUpdate("INSERT INTO Reservation VALUES(0, 'jung0420', '신용카드', 'paid', 12000, '2024-04-20');");
            stmt.executeUpdate("INSERT INTO Reservation VALUES(0, 'jung0210', '신용카드', 'paid', 12000, '2024-04-10');");
            stmt.executeUpdate("INSERT INTO Reservation VALUES(0, 'park0520', '계좌이체', 'paid', 12000, '2024-05-20');");
            stmt.executeUpdate("INSERT INTO Reservation VALUES(0, 'chul0621', '계좌이체', 'paid', 12000, '2024-06-02');");
            stmt.executeUpdate("INSERT INTO Reservation VALUES(0, 'haha0721', '신용카드', 'paid', 12000, '2024-06-11');");
            stmt.executeUpdate("INSERT INTO Reservation VALUES(0, 'jung0910', '신용카드', 'paid', 12000, '2024-06-10');");
            stmt.executeUpdate("INSERT INTO Reservation VALUES(0, 'yoo0111', '신용카드', 'paid', 12000, '2024-04-11');");
            stmt.executeUpdate("INSERT INTO Reservation VALUES(0, 'kang0121', '신용카드', 'paid', 12000, '2024-05-23');");
            stmt.executeUpdate("INSERT INTO Reservation VALUES(0, 'lee1121', '신용카드', 'paid', 12000, '2024-05-21');");
            stmt.executeUpdate("INSERT INTO Reservation VALUES(0, 'lee0810', '신용카드', 'paid', 12000, '2024-05-10');");
            stmt.executeUpdate("INSERT INTO Reservation VALUES(0, 'one1223', '신용카드', 'paid', 12000, '2024-05-23');");
            stmt.executeUpdate("INSERT INTO Reservation VALUES(0, 'kim0226', '신용카드', 'paid', 12000, '2024-04-28');");
            stmt.executeUpdate("INSERT INTO Reservation VALUES(0, 'kim0226', '신용카드', 'paid', 12000, '2024-04-26');");
            
            // 기본 상영일정 정보 추가
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 1, 1, '2024-06-01', '2024-06-24 (월)', 1, '10:00:00');");
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 1, 1, '2024-06-01', '2024-06-25 (화)', 1, '14:00:00');");
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 1, 1, '2024-06-01', '2024-06-26 (수)', 1, '18:00:00');");
            
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 2, 2, '2024-06-02', '2024-06-27 (목)', 1, '10:00:00');");
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 2, 2, '2024-06-02', '2024-06-28 (금)', 1, '14:00:00');");
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 2, 2, '2024-06-02', '2024-06-29 (토)', 1, '18:00:00');");

            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 3, 3, '2024-06-03', '2024-06-24 (월)', 1, '10:00:00');");
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 3, 3, '2024-06-03', '2024-06-25 (화)', 1, '14:00:00');");
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 3, 3, '2024-06-03', '2024-06-26 (수)', 1, '18:00:00');");

            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 4, 4, '2024-06-04', '2024-06-27 (목)', 1, '10:00:00');");
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 4, 4, '2024-06-04', '2024-06-28 (금)', 1, '14:00:00');");
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 4, 4, '2024-06-04', '2024-06-29 (토)', 1, '18:00:00');");

            
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 5, 5, '2024-06-05', '2024-06-24 (월)', 1, '10:00:00');");
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 5, 5, '2024-06-05', '2024-06-25 (화)', 1, '14:00:00');");
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 5, 5, '2024-06-05', '2024-06-26 (수)', 1, '18:00:00');");

            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 6, 6, '2024-06-06', '2024-06-27 (목)', 1, '10:00:00');");
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 6, 6, '2024-06-06', '2024-06-28 (금)', 1, '14:00:00');");
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 6, 6, '2024-06-06', '2024-06-29 (토)', 1, '18:00:00');");

            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 7, 7, '2024-06-07', '2024-06-24 (월)', 1, '10:00:00');");
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 7, 7, '2024-06-07', '2024-06-25 (화)', 1, '14:00:00');");
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 7, 7, '2024-06-07', '2024-06-26 (수)', 1, '18:00:00');");

            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 8, 8, '2024-06-08', '2024-06-27 (목)', 1, '10:00:00');");
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 8, 8, '2024-06-08', '2024-06-28 (금)', 1, '14:00:00');");
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 8, 8, '2024-06-08', '2024-06-29 (토)', 1, '18:00:00');");

            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 9, 9, '2024-06-09', '2024-06-24 (월)', 1, '10:00:00');");
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 9, 9, '2024-06-09', '2024-06-25 (화)', 1, '14:00:00');");
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 9, 9, '2024-06-09', '2024-06-26 (수)', 1, '18:00:00');");

            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 10, 10, '2024-06-10', '2024-06-27 (목)', 1, '10:00:00');");
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 10, 10, '2024-06-10', '2024-06-28 (금)', 1, '14:00:00');");
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 10, 10, '2024-06-10', '2024-06-29 (토)', 1, '18:00:00');");

            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 11, 11, '2024-06-11', '2024-06-24 (월)', 1, '10:00:00');");
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 11, 11, '2024-06-11', '2024-06-25 (화)', 1, '14:00:00');");
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 11, 11, '2024-06-11', '2024-06-26 (수)', 1, '18:00:00');");

            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 12, 12, '2024-06-12', '2024-06-27 (목)', 1, '10:00:00');");
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 12, 12, '2024-06-12', '2024-06-28 (금)', 1, '14:00:00');");
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 12, 12, '2024-06-12', '2024-06-29 (토)', 1, '18:00:00');");

            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 13, 13, '2024-06-13', '2024-06-24 (월)', 1, '10:00:00');");
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 13, 13, '2024-06-13', '2024-06-25 (화)', 1, '14:00:00');");
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 13, 13, '2024-06-13', '2024-06-26 (수)', 1, '18:00:00');");

            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 14, 14, '2024-06-14', '2024-06-27 (목)', 1, '10:00:00');");
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 14, 14, '2024-06-14', '2024-06-28 (금)', 1, '14:00:00');");
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 14, 14, '2024-06-14', '2024-06-29 (토)', 1, '18:00:00');");

            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 15, 15, '2024-06-15', '2024-06-24 (월)', 1, '10:00:00');");
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 15, 15, '2024-06-15', '2024-06-25 (화)', 1, '14:00:00');");
            stmt.executeUpdate("INSERT INTO Schedules VALUES(0, 15, 15, '2024-06-15', '2024-06-26 (수)', 1, '18:00:00');");
           
            // 기본 영화 정보 추가
            stmt.executeUpdate("INSERT INTO Movies VALUES(0, '범죄도시', '02:01:00', '청소년 관람불가', '강윤성', '마동석, 윤계상, 조재윤, 최귀화, 박지환', '범죄, 액션', '2004년 서울… 하얼빈에서 넘어와 단숨에 기존 조직들을 장악하고 가장 강력한 세력인 춘식이파 보스 황사장(조재윤 분)까지 위협하며 도시 일대의 최강자로 급부상한 신흥범죄조직의 악랄한 보스 장첸(윤계상 분). 대한민국을 뒤흔든 장첸(윤계상 분) 일당을 잡기 위해 오직 주먹 한방으로 도시의 평화를 유지해 온 괴물형사 마석도(마동석 분)와 인간미 넘치는 든든한 리더 전일만(최귀화 분) 반장이 이끄는 강력반은 나쁜 놈들을 한방에 쓸어버릴 끝.짱.나.는. 작전을 세우는데… 통쾌하고! 화끈하고! 살벌하게! 나쁜 놈들 때려잡는 강력반 형사들의 조폭소탕작전이 시작된다!', '2017-10-03', 9.28);");
            stmt.executeUpdate("INSERT INTO Movies VALUES(0, '범죄도시2', '01:46:00', '15세 이상', '이상용', '마동석, 손석구, 최귀화, 박지환', '범죄, 액션', '가리봉동 소탕작전 후 4년 뒤, 금천서 강력반은 베트남으로 도주한 용의자를 인도받아 오라는 미션을 받는다. 괴물형사 마석도(마동석)와 전일만(최귀화) 반장은 현지 용의자에게서 수상함을 느끼고, 그의 뒤에 무자비한 악행을 벌이는 강해상(손석구)이 있음을 알게 된다. 마석도와 금천서 강력반은 한국과 베트남을 오가며 역대급 범죄를 저지르는 강해상을 본격적으로 쫓기 시작하는데... 나쁜 놈들 잡는 데 국경 없다! 통쾌하고 화끈한 범죄 소탕 작전이 다시 펼쳐진다!', '2022-05-18', 8.98);");
            stmt.executeUpdate("INSERT INTO Movies VALUES(0, '범죄도시3', '01:45:00', '15세 이상', '이상용', '마동석, 이준혁, 이범수, 김민재, 박지환', '범죄, 액션', '대체불가 괴물형사 마석도, 서울 광수대로 발탁! 베트남 납치 살해범 검거 후 7년 뒤, 마석도(마동석)는 새로운 팀원들과 함께 살인사건을 조사한다. 사건 조사 중, 마석도는 신종 마약 사건이 연루되었음을 알게 되고 수사를 확대한다. 한편, 마약 사건의 배후인 주성철(이준혁)은 계속해서 판을 키워가고 약을 유통하던 일본 조직과 리키(아오키 무네타카)까지 한국에 들어오며 사건의 규모는 점점 더 커져가는데... 나쁜 놈들 잡는 데 이유 없고 제한 없다. 커진 판도 시원하게 싹 쓸어버린다!', '2023-05-31', 7.67);");
            stmt.executeUpdate("INSERT INTO Movies VALUES(0, '범죄도시4', '01:49:00', '15세 이상', '허명행', '마동석, 김무열, 이동휘, 이범수, 박지환', '범죄, 액션', '신종 마약 사건 3년 뒤, 괴물형사 마석도(마동석)와 서울 광수대는 배달앱을 이용한 마약 판매 사건을 수사하던 중 수배 중인 앱 개발자가 필리핀에서 사망한 사건이 대규모 온라인 불법 도박 조직과 연관되어 있음을 알아낸다. 필리핀에 거점을 두고 납치, 감금, 폭행, 살인 등으로 대한민국 온라인 불법 도박 시장을 장악한 특수부대 용병 출신의 빌런 백창기(김무열)와 한국에서 더 큰 판을 짜고 있는 IT업계 천재 CEO 장동철(이동휘). 나쁜 놈 잡는데 국경도 영역도 제한 없다! 업그레이드 소탕 작전! 거침없이 싹 쓸어버린다!', '2024-04-24', 7.56);");
            stmt.executeUpdate("INSERT INTO Movies VALUES(0, '베테랑', '02:03:00', '15세 이상', '류승완', '황정민, 유아인, 유해진, 오달수, 장윤주', '액션, 드라마', '한 번 꽂힌 것은 무조건 끝을 보는 행동파 서도철(황정민), 20년 경력의 승부사 오팀장(오달수), 위장 전문 홍일점 미스봉(장윤주), 육체파 왕형사(오대환), 막내 윤형사(김시후)까지 겁 없고, 못 잡는 것 없고, 봐 주는 것 없는 특수 강력사건 담당 광역수사대. 오랫동안 쫓던 대형 범죄를 해결한 후 숨을 돌리려는 찰나, 서도철은 재벌 3세 조태오(유아인)를 만나게 된다. 세상 무서울 것 없는 안하무인의 조태오와 언제나 그의 곁을 지키는 오른팔 최상무(유해진). 베테랑 광역수사대 VS 유아독존 재벌 3세 2015년 여름, 자존심을 건 한판 대결이 시작된다!', '2015-08-05', 9.24);");
            stmt.executeUpdate("INSERT INTO Movies VALUES(0, '신세계', '02:14:00', '청소년 관람불가', '박훈정', '이정재, 최민식, 황정민, 박성웅, 송지효', '범죄, 액션', '경찰청 수사 기획과 강과장(최민식)은 국내 최대 범죄 조직인 골드문이 기업형 조직으로 그 세력이 점점 확장되자 신입경찰 이자성(이정재)에게 잠입 수사를 명한다. 그리고 8년, 자성은 골드문의 2인자이자 그룹 실세인 정청(황정민)의 오른팔이 되기에 이른다. 시시각각 신분이 노출될 위기에 처한 자성(이정재)은 언제 자신을 배신할 지 모르는 경찰과, 형제의 의리로 대하는 정청(황정민) 사이에서 갈등하게 되는데…', '2013-02-21', 8.94);");
            stmt.executeUpdate("INSERT INTO Movies VALUES(0, '극한직업', '01:51:00', '15세 이상', '이병헌', '류승룡, 이하늬, 진선규, 이동휘, 공명', '코미디', '불철주야 달리고 구르지만 실적은 바닥, 급기야 해체 위기를 맞는 마약반! 더 이상 물러설 곳이 없는 팀의 맏형 고반장은 국제 범죄조직의 국내 마약 밀반입 정황을 포착하고 장형사, 마형사, 영호, 재훈까지 4명의 팀원들과 함께 잠복 수사에 나선다. 마약반은 24시간 감시를 위해 범죄조직의 아지트 앞 치킨집을 인수해 위장 창업을 하게 되고, 뜻밖의 절대미각을 지닌 마형사의 숨은 재능으로 치킨집은 일약 맛집으로 입소문이 나기 시작한다. 수사는 뒷전, 치킨장사로 눈코 뜰 새 없이 바빠진 마약반에게 어느 날 절호의 기회가 찾아오는데… 범인을 잡을 것인가, 닭을 잡을 것인가!', '2019-01-23', 9.20);");
            stmt.executeUpdate("INSERT INTO Movies VALUES(0, '도둑들', '02:15:00', '15세 이상', '최동훈', '김윤석, 김혜수, 이정재, 전지현, 김수현, 오달수', '범죄, 액션, 드라마', '한 팀으로 활동 중인 한국의 도둑 뽀빠이와 예니콜, 씹던껌, 잠파노. 미술관을 터는데 멋지게 성공한 이들은 뽀빠이의 과거 파트너였던 마카오박이 제안한 홍콩에서의 새로운 계획을 듣게 된다. 여기에 마카오박이 초대하지 않은 손님, 감옥에서 막 출소한 금고털이 팹시가 합류하고 5명은 각자 인생 최고의 반전을 꿈꾸며 홍콩으로 향한다. 홍콩에서 한국 도둑들을 기다리고 있는 4인조 중국도둑 첸, 앤드류, 쥴리, 조니. 최고의 전문가들이 세팅된 가운데 서로에 대한 경계를 늦추지 않는 한국과 중국의 도둑들. 팽팽히 흐르는 긴장감 속에 나타난 마카오박은 자신이 계획한 목표물을 밝힌다. 그것은 마카오 카지노에 숨겨진 희대의 다이아몬드 <태양의 눈물>. 성공을 장담할 수 없는 위험천만한 계획이지만 2천만 달러의 달콤한 제안을 거부할 수 없는 이들은 태양의 눈물을 훔치기 위한 작업에 착수한다. ', '2012-07-25', 7.66);");
            stmt.executeUpdate("INSERT INTO Movies VALUES(0, '국제시장', '02:06:00', '12세 이상', '윤제균', '황정민, 김윤진, 오달수, 정진영, 장영남, 라미란', '드라마', '1950년대 한국전쟁 이후로부터 현재에 이르기까지 격변의 시대를 관통하며 살아온 우리 시대 아버지 덕수(황정민 분), 그는 하고 싶은 것도 되고 싶은 것도 많았지만 평생 단 한번도 자신을 위해 살아본 적이 없다. 괜찮다 웃어 보이고 다행이다 눈물 훔치며 힘들었던 그때 그 시절, 오직 가족을 위해 굳세게 살아온 우리들의 아버지 이야기가 지금부터 시작된다.', '2014-12-17', 9.16);");
            stmt.executeUpdate("INSERT INTO Movies VALUES(0, '해운대', '02:00:00', '12세 이상', '윤제균', '설경구, 하지원, 박중훈, 엄정화, 이민기', '모험, 드라마', '2004년 역사상 유례없는 최대의 사상자를 내며 전세계에 엄청난 충격을 안겨준 인도네시아 쓰나미. 당시 인도양에 원양어선을 타고 나갔던 해운대 토박이 만식은 예기치 못한 쓰나미에 휩쓸리게 되고, 단 한 순간의 실수로 그가 믿고 의지했던 연희 아버지를 잃고 만다. 한편 국제해양연구소의 지질학자 김휘 박사는 대마도와 해운대를 둘러싼 동해의 상황이 5년전 발생했던 인도네시아 쓰나미와 흡사하다는 엄청난 사실을 발견하게 된다. 그는 대한민국도 쓰나미에 안전하지 않다고 수차례 강조하지만 그의 경고에도 불구하고 재난 방재청은 지질학적 통계적으로 쓰나미가 한반도를 덮칠 확률은 없다고 단언한다. 그 순간에도 바다의 상황은 시시각각 변해가고, 마침내 김휘 박사의 주장대로 일본 대마도가 내려 앉으면서 초대형 쓰나미가 생성된다.', '2009-07-22', 7.43);");
            stmt.executeUpdate("INSERT INTO Movies VALUES(0, '변호인', '02:07:00', '15세 이상', '양우석', '송강호, 김영애, 오달수, 곽도원, 임시완', '드라마', '1980년대 초 부산. 빽 없고, 돈 없고, 가방끈도 짧은 세무 변호사 송우석(송강호). 부동산 등기부터 세금 자문까지 남들이 뭐라든 탁월한 사업수완으로 승승장구하며 부산에서 제일 잘나가고 돈 잘 버는 변호사로 이름을 날린다. 대기업의 스카우트 제의까지 받으며 전국구 변호사 데뷔를 코 앞에 둔 송변. 하지만 우연히 7년 전 밥값 신세를 지며 정을 쌓은 국밥집 아들 진우(임시완)가 뜻하지 않은 사건에 휘말려 재판을 앞두고 있다는 소식을 듣는다. 국밥집 아줌마 순애(김영애)의 간절한 부탁을 외면할 수 없어 구치소 면회만이라도 도와주겠다고 나선 송변. 하지만 그곳에서 마주한 진우의 믿지 못할 모습에 충격을 받은 송변은 모두가 회피하기 바빴던 사건의 변호를 맡기로 결심하는데...', '2013-12-18', 9.30);");
            stmt.executeUpdate("INSERT INTO Movies VALUES(0, '부산행', '01:58:00', '15세 이상', '연상호', '공유, 정유미, 마동석, 김수안, 김의성', '액션, 스릴러', '정체불명의 바이러스가 전국으로 확산되고 대한민국 긴급재난경보령이 선포된 가운데, 열차에 몸을 실은 사람들은 단 하나의 안전한 도시 부산까지 살아가기 위한 치열한 사투를 벌이게 된다. 서울에서 부산까지의 거리 442KM 지키고 싶은, 지켜야만 하는 사람들의 극한의 사투!', '2016-07-20', 8.60);");
            stmt.executeUpdate("INSERT INTO Movies VALUES(0, '7번방의 선물', '02:07:00', '15세 이상', '이환경', '류승룡, 박신혜, 갈소원, 오달수, 박원상', '코미디', '최악의 흉악범들이 모인 교도소 7번방에 이상한 놈이 들어왔다! 그는 바로 6살 지능의 딸바보 용구! 평생 죄만 짓고 살아온 7번방 패밀리들에게 떨어진 미션은 바로 용구 딸 예승이를 외부인 절대 출입금지인 교도소에 반.입.하.는.것! 2013년 새해, 웃음과 감동 가득한 사상초유의 합동작전이 시작된다!', '2013-01-23', 8.83);");
            stmt.executeUpdate("INSERT INTO Movies VALUES(0, '기생충', '02:11:00', '15세 이상', '봉준호', '송강호, 이선균, 조여정, 최우식, 박소담, 정지소', '드라마', '전원백수로 살 길 막막하지만 사이는 좋은 기택(송강호) 가족. 장남 기우(최우식)에게 명문대생 친구가 연결시켜 준 고액 과외 자리는 모처럼 싹튼 고정수입의 희망이다. 온 가족의 도움과 기대 속에 박사장(이선균) 집으로 향하는 기우. 글로벌 IT기업 CEO인 박사장의 저택에 도착하자 젊고 아름다운 사모님 연교(조여정)가 기우를 맞이한다. 그러나 이렇게 시작된 두 가족의 만남 뒤로, 걷잡을 수 없는 사건이 기다리고 있었으니…', '2019-05-30', 9.07);");
            stmt.executeUpdate("INSERT INTO Movies VALUES(0, '명량', '02:08:00', '15세 이상', '김한민', '최민식, 류승룡, 조진웅, 진구, 이정현, 권율', '액션, 드라마', '1597년 임진왜란 6년, 오랜 전쟁으로 인해 혼란이 극에 달한 조선. 무서운 속도로 한양으로 북상하는 왜군에 의해 국가존망의 위기에 처하자 누명을 쓰고 파면 당했던 이순신 장군(최민식)이 삼도수군통제사로 재임명된다. 하지만 그에게 남은 건 전의를 상실한 병사와 두려움에 가득 찬 백성, 그리고 12척의 배 뿐. 마지막 희망이었던 거북선마저 불타고 잔혹한 성격과 뛰어난 지략을 지닌 용병 구루지마(류승룡)가 왜군 수장으로 나서자 조선은 더욱 술렁인다. 330척에 달하는 왜군의 배가 속속 집결하고 압도적인 수의 열세에 모두가 패배를 직감하는 순간, 이순신 장군은 단 12척의 배를 이끌고 명량 바다를 향해 나서는데…! 12척의 조선 vs 330척의 왜군 역사를 바꾼 위대한 전쟁이 시작된다!', '2014-07-30', 8.88);");
                          
            // 기본 상영관 정보 추가
            stmt.executeUpdate("INSERT INTO Theater VALUES(0, 60 ,'Y');");
            stmt.executeUpdate("INSERT INTO Theater VALUES(0, 60 ,'Y');");
            stmt.executeUpdate("INSERT INTO Theater VALUES(0, 60 ,'Y');");
            stmt.executeUpdate("INSERT INTO Theater VALUES(0, 60 ,'Y');");
            stmt.executeUpdate("INSERT INTO Theater VALUES(0, 60 ,'Y');");
            stmt.executeUpdate("INSERT INTO Theater VALUES(0, 120 ,'Y');");
            stmt.executeUpdate("INSERT INTO Theater VALUES(0, 120 ,'Y');");
            stmt.executeUpdate("INSERT INTO Theater VALUES(0, 120 ,'Y');");
            stmt.executeUpdate("INSERT INTO Theater VALUES(0, 120 ,'Y');");
            stmt.executeUpdate("INSERT INTO Theater VALUES(0, 120 ,'Y');");
            stmt.executeUpdate("INSERT INTO Theater VALUES(0, 180 ,'Y');");
            stmt.executeUpdate("INSERT INTO Theater VALUES(0, 180 ,'Y');");
            stmt.executeUpdate("INSERT INTO Theater VALUES(0, 180 ,'Y');");
            stmt.executeUpdate("INSERT INTO Theater VALUES(0, 180 ,'Y');");
            stmt.executeUpdate("INSERT INTO Theater VALUES(0, 180 ,'Y');");
            
            // 기본 좌석 정보 추가
            int[] theaterCapacities = {60, 60, 60, 60, 60, 120, 120, 120, 120, 120, 180, 180, 180, 180, 180};
            int[] scheduleCounts = {3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};
            int vIndex = 0;
            
            for (int theaterNumber = 1; theaterNumber <= theaterCapacities.length; theaterNumber++) {
                int capacity = theaterCapacities[theaterNumber - 1];
                int scheduleCount = scheduleCounts[theaterNumber - 1]; 
                for (int scheduleIndex = vIndex; scheduleIndex < vIndex + scheduleCount; scheduleIndex++) {
                    for (int i = 0; i < capacity; i++) {
                        int row = i / 30;
                        int num = i % 30 + 10;
                        String seatNumber = generateSeatNumber(row, num);
                        stmt.executeUpdate(String.format("INSERT INTO Seat (seat_number, theater_number, schedule_number, seat_status) VALUES ('%s', %d, %d, 'n');", seatNumber, theaterNumber, scheduleIndex + 1));
                    }
                }
                vIndex += 3 ;
            }
            
            String sql = "UPDATE seat s " +
                    "JOIN ticket t ON s.seat_number = t.seat_number " +
                    "AND s.schedule_number = t.schedule_number " +
                    "AND s.theater_number = t.theater_number " +
                    "SET s.seat_status = 'y'";
            stmt.executeUpdate(sql);
            
            stmt.executeUpdate("SET SQL_MODE=@OLD_SQL_MODE;");
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;");
            stmt.executeUpdate("SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;");

            JOptionPane.showMessageDialog(this, "데이터베이스가 초기화되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "데이터베이스 초기화 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    String generateSeatNumber(int row, int num) {
        return (char)('A' + row) + Integer.toString(num + 1);
    }

    private void executeUpdate(Statement stmt, String sql) throws SQLException {
        stmt.executeUpdate(sql);
    }

    // 전체 테이블 보기
    private void showAllTables() {
        resultPanel.removeAll();
        resultPanel.revalidate();
        resultPanel.repaint();

        // 다른 프로젝트 테스트 후 테이블이 남아 있을 수 있으므로 직접 정의한 특정 테이블들 목록만 따로 정의
        String[] tableNames = {"members", "movies", "reservation", "theater", "schedules", "seat", "ticket"};

        for (String tableName : tableNames) {
            try (Statement tableStmt = conn.createStatement()) {
                ResultSet tableRs = tableStmt.executeQuery("SELECT * FROM " + tableName);
                ResultSetMetaData metaData = tableRs.getMetaData();
                int columnCount = metaData.getColumnCount();

                java.util.List<Object[]> dataList = new java.util.ArrayList<>();
                while (tableRs.next()) {
                    Object[] rowData = new Object[columnCount];
                    for (int i = 0; i < columnCount; i++) {
                        rowData[i] = tableRs.getObject(i + 1);
                    }
                    dataList.add(rowData);
                }

                Object[][] data = new Object[dataList.size()][columnCount];
                dataList.toArray(data);

                String[] columnNames = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    columnNames[i] = metaData.getColumnName(i + 1);
                }

                JTable table = new JTable(data, columnNames);
                table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                JScrollPane scrollPane = new JScrollPane(table);

                resultPanel.add(new JLabel(tableName));
                resultPanel.add(scrollPane);

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, tableName + " 테이블 데이터를 조회 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        }

        resultPanel.revalidate();
        resultPanel.repaint();
    }
    
    // 데이터 삭제 화면 
    private void showDeleteDialog() {
        JDialog deleteDialog = new JDialog(this, "데이터 삭제", true);
        deleteDialog.setSize(600, 300); 
        deleteDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel deleteLabel = new JLabel("Delete");
        JLabel fromLabel = new JLabel("from");
        JLabel whereLabel = new JLabel("where");

        JTextField tableField = new JTextField("members", 30); 
        JTextArea conditionField = new JTextArea("member_id = 'mong1022'", 3, 30); 
        conditionField.setLineWrap(true);
        conditionField.setWrapStyleWord(true);
        JScrollPane conditionScrollPane = new JScrollPane(conditionField);

        JButton executeButton = new JButton("삭제 실행");
        JButton cancelButton = new JButton("삭제 취소");

        deleteDialog.add(deleteLabel, gbc);
        gbc.gridy++;
        deleteDialog.add(fromLabel, gbc);
        gbc.gridx++;
        deleteDialog.add(tableField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        deleteDialog.add(whereLabel, gbc);
        gbc.gridx++;
        deleteDialog.add(conditionScrollPane, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.add(executeButton);
        buttonPanel.add(cancelButton);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2; 
        gbc.anchor = GridBagConstraints.CENTER; 
        deleteDialog.add(buttonPanel, gbc);

        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tableName = tableField.getText();
                String condition = conditionField.getText();
                if (!tableName.isEmpty()) {
                    deleteData(tableName, condition);
                    deleteDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(deleteDialog, "테이블 이름을 입력해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	tableField.setText("members"); 
                conditionField.setText("member_id = 'mong1022'"); 
            }
        });

        deleteDialog.setLocationRelativeTo(this);
        deleteDialog.setVisible(true);
    }

    // 데이터 삭제 메인 기능 
    private void deleteData(String tableName, String condition) {
        try (Statement stmt = conn.createStatement()) {
            String sql;
            if (condition.isEmpty()) {
                // 조건이 없을 경우, 테이블의 모든 행 삭제
                sql = "DELETE FROM " + tableName;
            } else {
                // 조건이 있을 경우, 조건에 맞는 행 삭제
                sql = "DELETE FROM " + tableName + " WHERE " + condition;
            }

            if (tableName.equalsIgnoreCase("reservation")) {
                // 예약과 관련된 티켓 조회
                String selectTicketsSql = "SELECT theater_number, seat_number, schedule_number FROM ticket WHERE reservation_number IN (SELECT reservation_number FROM reservation WHERE " + condition + ")";
                try (Statement selectStmt = conn.createStatement();
                     ResultSet rs = selectStmt.executeQuery(selectTicketsSql)) {
                    while (rs.next()) {
                        int theaterNumber = rs.getInt("theater_number");
                        String seatNumber = rs.getString("seat_number");
                        int scheduleNumber = rs.getInt("schedule_number");

                        // 기존 좌석 상태를 'n'으로 업데이트
                        String updateSeatSql = "UPDATE seat SET seat_status = 'n' WHERE theater_number = " + theaterNumber + " AND seat_number = '" + seatNumber + "' AND schedule_number = " + scheduleNumber;
                        try (Statement updateStmt = conn.createStatement()) {
                            updateStmt.executeUpdate(updateSeatSql);
                        }
                    }
                }

                // 관련된 티켓 삭제
                String deleteTicketsSql = "DELETE FROM ticket WHERE reservation_number IN (SELECT reservation_number FROM reservation WHERE " + condition + ")";
                stmt.executeUpdate(deleteTicketsSql);
            } else if (tableName.equalsIgnoreCase("ticket")) {
                // 티켓에 해당하는 좌석 정보 조회
                String selectSql = "SELECT theater_number, seat_number, schedule_number FROM ticket WHERE " + condition;
                try (Statement selectStmt = conn.createStatement();
                     ResultSet rs = selectStmt.executeQuery(selectSql)) {
                    while (rs.next()) {
                        int theaterNumber = rs.getInt("theater_number");
                        String seatNumber = rs.getString("seat_number");
                        int scheduleNumber = rs.getInt("schedule_number");

                        // 기존 좌석 상태를 'n'으로 업데이트
                        String updateSql = "UPDATE seat SET seat_status = 'n' WHERE theater_number = " + theaterNumber + " AND seat_number = '" + seatNumber + "' AND schedule_number = " + scheduleNumber;
                        try (Statement updateStmt = conn.createStatement()) {
                            updateStmt.executeUpdate(updateSql);
                        }
                    }
                }
            } else if (tableName.equalsIgnoreCase("members")) {
                // 멤버와 관련된 예약 정보 조회
                String selectReservationsSql = "SELECT reservation_number FROM reservation WHERE member_id IN (SELECT member_id FROM members WHERE " + condition + ")";
                List<Integer> reservationNumbers = new ArrayList<>();
                try (Statement selectStmt = conn.createStatement();
                     ResultSet rs = selectStmt.executeQuery(selectReservationsSql)) {
                    while (rs.next()) {
                        reservationNumbers.add(rs.getInt("reservation_number"));
                    }
                }

                for (int reservationNumber : reservationNumbers) {
                    // 예약에 해당하는 티켓 정보 조회
                    String selectTicketsSql = "SELECT theater_number, seat_number, schedule_number FROM ticket WHERE reservation_number = " + reservationNumber;
                    List<String[]> ticketInfo = new ArrayList<>();
                    try (Statement selectStmt = conn.createStatement();
                         ResultSet ticketRs = selectStmt.executeQuery(selectTicketsSql)) {
                        while (ticketRs.next()) {
                            int theaterNumber = ticketRs.getInt("theater_number");
                            String seatNumber = ticketRs.getString("seat_number");
                            int scheduleNumber = ticketRs.getInt("schedule_number");
                            ticketInfo.add(new String[]{String.valueOf(theaterNumber), seatNumber, String.valueOf(scheduleNumber)});
                        }
                    }

                    for (String[] info : ticketInfo) {
                        // 좌석 상태를 'n'으로 업데이트
                        String updateSeatSql = "UPDATE seat SET seat_status = 'n' WHERE theater_number = " + info[0] + " AND seat_number = '" + info[1] + "' AND schedule_number = " + info[2];
                        try (Statement updateStmt = conn.createStatement()) {
                            updateStmt.executeUpdate(updateSeatSql);
                        }
                    }

                    // 관련된 티켓 삭제
                    String deleteTicketsSql = "DELETE FROM ticket WHERE reservation_number = " + reservationNumber;
                    try (Statement deleteStmt = conn.createStatement()) {
                        deleteStmt.executeUpdate(deleteTicketsSql);
                    }
                }

                // 관련된 예약 삭제
                String deleteReservationsSql = "DELETE FROM reservation WHERE member_id IN (SELECT member_id FROM members WHERE " + condition + ")";
                stmt.executeUpdate(deleteReservationsSql);
            }

            // 최종적으로 지정된 테이블에서 데이터 삭제
            int rowsAffected = stmt.executeUpdate(sql);

            JOptionPane.showMessageDialog(this, rowsAffected + "개의 데이터가 삭제되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "데이터 삭제 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // 데이터 변경 화면
    private void showUpdateDialog() {
        JDialog updateDialog = new JDialog(this, "데이터 변경", true);
        updateDialog.setSize(600, 300); 
        updateDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST; 

        JLabel updateLabel = new JLabel("Update");
        JLabel setLabel = new JLabel("set");
        JLabel whereLabel = new JLabel("where");

        JComboBox<String> tableComboBox = new JComboBox<>(getTableNames()); 
        JTextArea setField = new JTextArea("member_name = '하동훈'", 3, 30); 
        setField.setLineWrap(true);
        setField.setWrapStyleWord(true);
        JScrollPane setScrollPane = new JScrollPane(setField);

        JTextArea conditionField = new JTextArea("member_name = '하하'", 3, 30); 
        conditionField.setLineWrap(true);
        conditionField.setWrapStyleWord(true);
        JScrollPane conditionScrollPane = new JScrollPane(conditionField);

        JButton executeButton = new JButton("변경 실행");
        JButton cancelButton = new JButton("변경 취소");

        gbc.gridx = 0;
        gbc.gridy = 0;
        updateDialog.add(updateLabel, gbc);
        gbc.gridx++;
        updateDialog.add(tableComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        updateDialog.add(setLabel, gbc);
        gbc.gridx++;
        updateDialog.add(setScrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        updateDialog.add(whereLabel, gbc);
        gbc.gridx++;
        updateDialog.add(conditionScrollPane, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.add(executeButton);
        buttonPanel.add(cancelButton);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2; 
        gbc.anchor = GridBagConstraints.CENTER; 
        updateDialog.add(buttonPanel, gbc);

        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tableName = (String) tableComboBox.getSelectedItem();
                String setClause = setField.getText();
                String condition = conditionField.getText();
                if (tableName != null && !setClause.isEmpty()) {
                    updateData(tableName, setClause, condition);
                    updateDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(updateDialog, "테이블 이름, SET 절을 입력해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setField.setText("member_name = '하동훈'");
                conditionField.setText("member_name = '하하'");
            }
        });

        updateDialog.setLocationRelativeTo(this);
        updateDialog.setVisible(true);
    }

    // 데이터 변경 메인 기능
    private void updateData(String tableName, String setClause, String condition) {
        if ("ticket".equalsIgnoreCase(tableName)) {
            try (Statement stmt = conn.createStatement()) {              
                String selectOriginalSQL = "SELECT seat_number, schedule_number, theater_number FROM " + tableName + " WHERE " + condition;
                ResultSet originalResultSet = stmt.executeQuery(selectOriginalSQL);
                
                String originalSeatNumber = null;
                int originalScheduleNumber = 0;
                int originalTheaterNumber = 0;
                
                if (originalResultSet.next()) {
                    originalSeatNumber = originalResultSet.getString("seat_number");
                    originalScheduleNumber = originalResultSet.getInt("schedule_number");
                    originalTheaterNumber = originalResultSet.getInt("theater_number");
                }
                
                String updateSQL = "UPDATE " + tableName + " SET " + setClause + " WHERE " + condition;
                int rowsAffected = stmt.executeUpdate(updateSQL);
                
                String newSeatNumber = originalSeatNumber;
                int newScheduleNumber = originalScheduleNumber;
                int newTheaterNumber = originalTheaterNumber;
                
                String[] setParts = setClause.split(",");
                for (String part : setParts) {
                    if (part.contains("seat_number")) {
                        newSeatNumber = part.split("=")[1].trim().replace("'", "");
                    } else if (part.contains("schedule_number")) {
                        newScheduleNumber = Integer.parseInt(part.split("=")[1].trim());
                    } else if (part.contains("theater_number")) {
                        newTheaterNumber = Integer.parseInt(part.split("=")[1].trim());
                    }
                }
                
                // 기존 좌석의 seat_status를 'n'으로 업데이트
                if (originalSeatNumber != null) {
                    String updateOriginalSeatStatusSQL = "UPDATE seat SET seat_status = 'n' WHERE seat_number = '" + originalSeatNumber + "' AND schedule_number = " + originalScheduleNumber + " AND theater_number = " + originalTheaterNumber;
                    stmt.executeUpdate(updateOriginalSeatStatusSQL);
                }
                
                // 업데이트된 좌석의 seat_status를 'y'으로 업데이트
                String updateNewSeatStatusSQL = "UPDATE seat SET seat_status = 'y' WHERE seat_number = '" + newSeatNumber + "' AND schedule_number = " + newScheduleNumber + " AND theater_number = " + newTheaterNumber;
                stmt.executeUpdate(updateNewSeatStatusSQL);
                
                JOptionPane.showMessageDialog(this, rowsAffected + "개의 데이터가 변경되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "데이터 변경 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // 티켓 테이블이 아닌 일반적인 경우
            try (Statement stmt = conn.createStatement()) {
                String sql;
                if (condition.isEmpty()) {
                    sql = "UPDATE " + tableName + " SET " + setClause;
                } else {
                    sql = "UPDATE " + tableName + " SET " + setClause + " WHERE " + condition;
                }
                int rowsAffected = stmt.executeUpdate(sql);
                JOptionPane.showMessageDialog(this, rowsAffected + "개의 데이터가 변경되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "데이터 변경 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 데이터베이스에서 테이블 이름을 가져오기
    private String[] getTableNames() {
        List<String> tableNames = new ArrayList<>();
        try (ResultSet rs = conn.getMetaData().getTables(null, null, "%", new String[]{"TABLE"})) {
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                // 실제 여기서 사용되는 테이블명들만 지정
                if (tableName.equals("members") || tableName.equals("movies") || tableName.equals("reservation") || 
                    tableName.equals("theater") || tableName.equals("schedules") || tableName.equals("seat") || 
                    tableName.equals("ticket")) {
                    tableNames.add(tableName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "테이블 목록을 가져오는 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
        return tableNames.toArray(new String[0]);
    }

    // 데이터 삽입 화면
    private void showInsertDialog() {
        JDialog dialog = new JDialog(this, "데이터 입력", true);
        dialog.setSize(600, 400);  
        dialog.setLocationRelativeTo(this);  

        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel tableLabel = new JLabel("(테이블 선택) INSERT INTO");
        JComboBox<String> tableComboBox = new JComboBox<>(new String[]{"members", "movies", "reservation", "theater", "schedules", "seat", "ticket"});

        panel.add(tableLabel);
        panel.add(tableComboBox);

        JButton submitButton = new JButton("입력 실행");
        submitButton.setEnabled(false);
        
        JButton cancelButton = new JButton("입력 취소");

        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        JScrollPane fieldsScrollPane = new JScrollPane(fieldsPanel);

        tableComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedTable = (String) tableComboBox.getSelectedItem();
                List<JTextArea> textFields = new ArrayList<>();
                fieldsPanel.removeAll();

                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("DESCRIBE " + selectedTable)) {

                    while (rs.next()) {
                        String fieldName = rs.getString("Field");
                        String fieldType = rs.getString("Type");
                        JLabel fieldLabel = new JLabel(fieldName);

                        JTextArea fieldInput = new JTextArea(2, 20);  
                        fieldInput.setLineWrap(true);  
                        fieldInput.setWrapStyleWord(true);

                        // 테스팅의 편의를 위해 디폴트값 설정
                        switch (fieldName) {
                            case "member_id":
                                fieldInput.setText("kim0501");
                                break;
                            case "member_name":
                                fieldInput.setText("김세종");
                                break;
                            case "phone_number":
                                fieldInput.setText("010-4444-1234");
                                break;
                            case "email":
                                fieldInput.setText("kim0501@gmail.com");
                                break;
                            case "movie_number":
                                fieldInput.setText("0");
                                break;
                            case "movie_title":
                                fieldInput.setText("서울의 봄");
                                break;
                            case "running_time":
                                fieldInput.setText("02:21:00");
                                break;
                            case "screening_rating":
                                fieldInput.setText("12세 이상");
                                break;
                            case "director":
                                fieldInput.setText("김성수");
                                break;
                            case "actor":
                                fieldInput.setText("황정민, 정우성, 이성민, 박해준, 김성균");
                                break;
                            case "genre":
                                fieldInput.setText("드라마");
                                break;
                            case "movie_introduction":
                                fieldInput.setText("1979년 12월 12일, 수도 서울 군사반란 발생 그날, 대한민국의 운명이 바뀌었다 권력에 눈이 먼 전두광의 반란군과 이에 맞선 수도경비사령관 이태신을 비롯한 진압군 사이, 일촉즉발의 9시간이 흘러가는데… 목숨을 건 두 세력의 팽팽한 대립 오늘 밤, 대한민국 수도에서 가장 치열한 전쟁이 펼쳐진다!");
                                break;
                            case "release_date":
                                fieldInput.setText("2023-11-22");
                                break;
                            case "movie_rating":
                                fieldInput.setText("9.50");
                                break;
                            case "reservation_number":
                                fieldInput.setText("0");
                                break;
                            case "payment_method":
                                fieldInput.setText("신용카드");
                                break;
                            case "payment_status":
                                fieldInput.setText("pending");
                                break;
                            case "payment_amount":
                                fieldInput.setText("12000");
                                break;
                            case "payment_date":
                                fieldInput.setText("2024-02-26");
                                break;
                            case "theater_number":
                                fieldInput.setText("0");
                                break;
                            case "theater_capacity":
                                fieldInput.setText("60");
                                break;
                            case "theater_status":
                                fieldInput.setText("Y");
                                break;
                            case "schedule_number":
                                fieldInput.setText("0");
                                break;
                            case "screening_start_date":
                                fieldInput.setText("2024-06-01");
                                break;
                            case "screening_day":
                                fieldInput.setText("2024-06-27 (목)");
                                break;
                            case "screening_round":
                                fieldInput.setText("1");
                                break;
                            case "screening_start_time":
                                fieldInput.setText("20:00:00");
                                break;
                            case "seat_number":
                                fieldInput.setText("A1");
                                break;
                            case "seat_status":
                                fieldInput.setText("n");
                                break;
                            case "ticket_number":
                                fieldInput.setText("0");
                                break;
                            case "ticket_status":
                                fieldInput.setText("Booked");
                                break;
                            case "standard_price":
                                fieldInput.setText("12000");
                                break;
                            case "sale_price":
                                fieldInput.setText("10000");
                                break;
                            default:
                                fieldInput.setText("default_value");
                                break;
                        }

                        textFields.add(fieldInput);

                        fieldsPanel.add(fieldLabel);
                        fieldsPanel.add(new JScrollPane(fieldInput));  
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog, "테이블 필드를 불러오는 중 오류가 발생했습니다: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }

                submitButton.setEnabled(true);
                fieldsPanel.revalidate();
                fieldsPanel.repaint();
            }
        });

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedTable = (String) tableComboBox.getSelectedItem();
                List<String> values = new ArrayList<>();

                for (Component component : fieldsPanel.getComponents()) {
                    if (component instanceof JScrollPane) {
                        JScrollPane scrollPane = (JScrollPane) component;
                        JViewport viewport = scrollPane.getViewport();
                        if (viewport.getView() instanceof JTextArea) {
                            JTextArea textArea = (JTextArea) viewport.getView();
                            values.add("'" + textArea.getText() + "'");
                        }
                    }
                }

                String sql = String.format("INSERT INTO %s VALUES (%s)", selectedTable, String.join(", ", values));

                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(sql);
                    JOptionPane.showMessageDialog(dialog, "데이터가 성공적으로 입력되었습니다.");
                    dialog.dispose();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog, "데이터 입력 중 오류가 발생했습니다: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Component component : fieldsPanel.getComponents()) {
                    if (component instanceof JScrollPane) {
                        JScrollPane scrollPane = (JScrollPane) component;
                        JViewport viewport = scrollPane.getViewport();
                        if (viewport.getView() instanceof JTextArea) {
                            JTextArea textArea = (JTextArea) viewport.getView();
                            textArea.setText("");
                        }
                    }
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.NORTH);
        dialog.add(fieldsScrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

}