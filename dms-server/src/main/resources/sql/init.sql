-- ============================================================
-- 社員寮管理システム（DMS）数据库初始化脚本
-- 数据库：MySQL 8.0+
-- 字符集：utf8mb4
-- ============================================================

CREATE DATABASE IF NOT EXISTS dms_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE dms_db;

-- ============================================================
-- 1. 寮信息表
-- ============================================================
DROP TABLE IF EXISTS dorm_info;
CREATE TABLE dorm_info (
    id           BIGINT       NOT NULL AUTO_INCREMENT        COMMENT '主键ID',
    name         VARCHAR(100) NOT NULL                       COMMENT '寮名称',
    address      VARCHAR(200) NOT NULL                       COMMENT '地址',
    type         VARCHAR(20)  NOT NULL                       COMMENT '类型：male/female/mixed',
    layout       VARCHAR(20)                                 COMMENT '户型：1K/1DK/2DK/3DK/2LDK/3LDK',
    region       VARCHAR(20)  NOT NULL                       COMMENT '地区：tokyo/osaka',
    total_rooms  INT          NOT NULL DEFAULT 0             COMMENT '总部屋数',
    status       TINYINT      NOT NULL DEFAULT 1             COMMENT '状态：1=启用，0=停用',
    is_deleted   TINYINT      NOT NULL DEFAULT 0             COMMENT '逻辑删除：0=正常，1=已删除',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_region (region),
    INDEX idx_type   (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='寮信息表';

-- ============================================================
-- 2. 部屋信息表
-- ============================================================
DROP TABLE IF EXISTS room_info;
CREATE TABLE room_info (
    id           BIGINT       NOT NULL AUTO_INCREMENT        COMMENT '主键ID',
    dorm_id      BIGINT       NOT NULL                       COMMENT '所属寮ID',
    dorm_name    VARCHAR(100)                                COMMENT '寮名称（冗余）',
    name         VARCHAR(50)  NOT NULL                       COMMENT '部屋名称',
    area         DECIMAL(5,1)                                COMMENT '面积（㎡）',
    status       VARCHAR(20)  NOT NULL DEFAULT 'vacant'      COMMENT '状态：vacant=空き/occupied=入居中',
    gender       VARCHAR(10)                                 COMMENT '适用性别：male/female',
    is_deleted   TINYINT      NOT NULL DEFAULT 0             COMMENT '逻辑删除',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_dorm_id (dorm_id),
    INDEX idx_status  (status),
    INDEX idx_gender  (gender)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部屋信息表';

-- ============================================================
-- 3. 社員信息表（日本/中国出张社員共用）
-- ============================================================
DROP TABLE IF EXISTS employee_info;
CREATE TABLE employee_info (
    id              BIGINT       NOT NULL AUTO_INCREMENT    COMMENT '主键ID',
    emp_id          VARCHAR(20)  NOT NULL                   COMMENT '工号',
    name            VARCHAR(50)  NOT NULL                   COMMENT '姓名',
    type            VARCHAR(10)  NOT NULL                   COMMENT '类型：jp=日本社員/cn=中国出张',
    gender          VARCHAR(10)                             COMMENT '性别：male/female',
    -- 日本社員专属字段
    dept            VARCHAR(50)                             COMMENT '所属部门（jp）',
    division        VARCHAR(50)                             COMMENT '所属课（jp）',
    emp_type        VARCHAR(20)                             COMMENT '雇用形态：regular/contract（jp）',
    mobile          VARCHAR(20)                             COMMENT '手机号（jp）',
    email           VARCHAR(100)                            COMMENT '邮箱（jp）',
    first_check_in  DATE                                    COMMENT '首次入寮日期（jp）',
    -- 中国社員专属字段
    origin          VARCHAR(20)                             COMMENT '来源地：shenyang/dalian（cn）',
    biz_type        VARCHAR(20)                             COMMENT '出张类型：longterm/shortterm（cn）',
    arrival_date    DATE                                    COMMENT '来日日期（cn）',
    departure_date  DATE                                    COMMENT '预定回国日期（cn）',
    -- 公共字段
    is_deleted      TINYINT      NOT NULL DEFAULT 0         COMMENT '逻辑删除',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE INDEX uk_emp_id (emp_id),
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社員信息表';

-- ============================================================
-- 4. 入退寮记录表
-- ============================================================
DROP TABLE IF EXISTS occupancy_record;
CREATE TABLE occupancy_record (
    id             BIGINT       NOT NULL AUTO_INCREMENT    COMMENT '主键ID',
    emp_id         BIGINT       NOT NULL                   COMMENT '社員ID',
    emp_name       VARCHAR(50)                             COMMENT '社員姓名（冗余）',
    emp_type       VARCHAR(10)                             COMMENT '社員类型（冗余）',
    dorm_id        BIGINT                                  COMMENT '寮ID（冗余）',
    dorm_name      VARCHAR(100)                            COMMENT '寮名称（冗余）',
    room_id        BIGINT       NOT NULL                   COMMENT '部屋ID',
    room_name      VARCHAR(50)                             COMMENT '部屋名称（冗余）',
    check_in_date  DATE         NOT NULL                   COMMENT '入寮日期',
    check_out_date DATE                                    COMMENT '退寮日期（NULL=入居中）',
    status         VARCHAR(20)  NOT NULL DEFAULT 'active'  COMMENT '状态：active=入居中/checked_out=已退寮',
    reason         VARCHAR(100)                            COMMENT '退寮事由',
    is_deleted     TINYINT      NOT NULL DEFAULT 0         COMMENT '逻辑删除',
    create_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_emp_id        (emp_id),
    INDEX idx_room_id       (room_id),
    INDEX idx_status        (status),
    INDEX idx_check_in_date (check_in_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入退寮记录表';

-- ============================================================
-- 5. 寮費记录表
-- ============================================================
DROP TABLE IF EXISTS fee_record;
CREATE TABLE fee_record (
    id           BIGINT       NOT NULL AUTO_INCREMENT    COMMENT '主键ID',
    year         INT          NOT NULL                   COMMENT '年份',
    month        INT          NOT NULL                   COMMENT '月份（1-12）',
    emp_id       BIGINT       NOT NULL                   COMMENT '社員ID',
    emp_name     VARCHAR(50)                             COMMENT '社員姓名（冗余）',
    emp_dept     VARCHAR(50)                             COMMENT '部门（冗余）',
    dorm_name    VARCHAR(100)                            COMMENT '寮名称（冗余）',
    room_name    VARCHAR(50)                             COMMENT '部屋名称（冗余）',
    area         DECIMAL(5,1)                            COMMENT '面积（㎡）',
    days         INT                                     COMMENT '入居天数',
    total_days   INT                                     COMMENT '当月总天数',
    unit_price   INT          NOT NULL DEFAULT 2000      COMMENT '单价（円/㎡）',
    amount       INT                                     COMMENT '寮費金额（円）',
    status       VARCHAR(20)  NOT NULL DEFAULT 'pending' COMMENT '状态：pending=待确定/confirmed=已确定',
    is_deleted   TINYINT      NOT NULL DEFAULT 0         COMMENT '逻辑删除',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_year_month (year, month),
    INDEX idx_emp_id     (emp_id),
    INDEX idx_status     (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='寮費记录表';

-- ============================================================
-- 初始化测试数据
-- ============================================================

-- 寮数据
INSERT INTO dorm_info (name, address, type, layout, region, total_rooms) VALUES
('江東区大島寮',   '東京都江東区大島3-1-1',   'male',   '3DK', 'tokyo', 4),
('江東区豊洲寮',   '東京都江東区豊洲5-2-3',   'female', '2DK', 'tokyo', 3),
('葛飾区青砥寮',   '東京都葛飾区青砥7-3-2',   'male',   '3DK', 'tokyo', 4),
('城東区森之宮寮', '大阪府城東区森之宮1-4-5', 'mixed',  '2DK', 'osaka', 4);

-- 部屋数据
INSERT INTO room_info (dorm_id, dorm_name, name, area, status, gender) VALUES
(1,'江東区大島寮','洋室A',   18.5,'occupied','male'),
(1,'江東区大島寮','洋室B',   18.5,'vacant',  'male'),
(1,'江東区大島寮','小和室',  12.0,'occupied','male'),
(1,'江東区大島寮','中和室',  15.0,'occupied','male'),
(2,'江東区豊洲寮','手前洋室',20.0,'occupied','female'),
(2,'江東区豊洲寮','洋室',    18.0,'vacant',  'female'),
(2,'江東区豊洲寮','小部屋',  10.5,'vacant',  'female'),
(3,'葛飾区青砥寮','洋室A',   16.5,'occupied','male'),
(3,'葛飾区青砥寮','洋室B',   16.5,'occupied','male'),
(3,'葛飾区青砥寮','和室',    14.0,'vacant',  'male'),
(3,'葛飾区青砥寮','洋室(荷物有)',16.5,'vacant','male'),
(4,'城東区森之宮寮','洋室A(男)',17.0,'occupied','male'),
(4,'城東区森之宮寮','洋室B(男)',17.0,'vacant',  'male'),
(4,'城東区森之宮寮','洋室A(女)',17.0,'occupied','female'),
(4,'城東区森之宮寮','洋室B(女)',17.0,'vacant',  'female');

-- 日本社員数据
INSERT INTO employee_info (emp_id, name, type, gender, dept, division, emp_type, mobile, email, first_check_in) VALUES
('E001','田中太郎','jp','male','本社','総務部','regular','090-1234-5678','tanaka@co.jp','2021-04-01'),
('E002','鈴木花子','jp','female','DX','DX推進部','regular','090-2345-6789','suzuki@co.jp','2022-07-01'),
('E003','佐藤健一','jp','male','本社','営業部','contract','090-3456-7890','sato@co.jp','2023-01-15'),
('E004','高橋美香','jp','female','CDXT','CDXT技術部','regular','090-4567-8901','takahashi@co.jp','2020-10-01'),
('E005','伊藤雄介','jp','male','DX','システム部','regular','090-5678-9012','ito@co.jp','2019-04-01');

-- 中国出张社員数据
INSERT INTO employee_info (emp_id, name, type, gender, origin, biz_type, arrival_date, departure_date) VALUES
('C001','王 磊','cn','male','shenyang','longterm','2024-01-10','2024-12-31'),
('C002','李 芳','cn','female','dalian','shortterm','2025-03-01','2025-06-30'),
('C003','張 偉','cn','male','shenyang','longterm','2024-06-15','2025-06-14'),
('C004','劉 洋','cn','male','dalian','shortterm','2025-04-01','2025-07-31');

-- 入退寮记录（当前入居中）
INSERT INTO occupancy_record (emp_id, emp_name, emp_type, dorm_id, dorm_name, room_id, room_name, check_in_date, status) VALUES
(1,'田中太郎','jp',1,'江東区大島寮',  1,'洋室A',    '2021-04-01','active'),
(2,'鈴木花子','jp',2,'江東区豊洲寮',  5,'手前洋室','2022-07-01','active'),
(3,'佐藤健一','jp',1,'江東区大島寮',  3,'小和室',   '2023-01-15','active'),
(4,'高橋美香','jp',4,'城東区森之宮寮',14,'洋室A(女)','2020-10-01','active'),
(5,'伊藤雄介','jp',1,'江東区大島寮',  4,'中和室',   '2019-04-01','active'),
(6,'王 磊',   'cn',3,'葛飾区青砥寮', 8,'洋室A',    '2024-01-10','active'),
(8,'張 偉',   'cn',3,'葛飾区青砥寮', 9,'洋室B',    '2024-06-15','active'),
(9,'劉 洋',   'cn',4,'城東区森之宮寮',12,'洋室A(男)','2025-04-01','active'),
(7,'李 芳',   'cn',4,'城東区森之宮寮',14,'洋室A(女)','2025-03-01','active');

-- 入退寮记录（已退寮）
INSERT INTO occupancy_record (emp_id, emp_name, emp_type, dorm_id, dorm_name, room_id, room_name, check_in_date, check_out_date, status, reason) VALUES
(1,'山田一郎','jp',2,'江東区豊洲寮',  6,'洋室',    '2020-04-01','2023-03-31','checked_out','退職'),
(2,'中村春子','jp',1,'江東区大島寮',  2,'洋室B',   '2021-07-01','2024-06-30','checked_out','異動'),
(6,'趙 明',  'cn',3,'葛飾区青砥寮', 10,'和室',    '2023-05-01','2024-04-30','checked_out','出張終了');
