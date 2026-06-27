# func_server.md — DMS 后端功能清单

> **用途**：后端代码生成/修改时的唯一事实来源。新增任何 Controller / Service 方法 / Mapper / Entity / 常量，必须同步更新本文档。
> **版本**：v1.0 最后更新：2026-06-06

---

## 一、项目信息

| 项目   | 值                                                    |
| ------ | ----------------------------------------------------- |
| 项目名 | dms-server                                            |
| 包路径 | com.dms.server                                        |
| 技术栈 | Spring Boot 2.7.18 + MyBatis + MySQL + Redis + Lombok |
| 端口   | 8080                                                  |
| 数据库 | dms_db（MySQL 8.0+）                                  |

---

## 二、Controller 层（接口清单）

### 2.1 DormController `/api/dorm`

| 方法        | 路径           | HTTP   | 入参         | 返回                 | 说明                         |
| ----------- | -------------- | ------ | ------------ | -------------------- | ---------------------------- |
| getDormList | /api/dorm/list | GET    | DormQueryDTO | Result<List<DormVO>> | 分页查询寮列表，含入居部屋数 |
| addDorm     | /api/dorm      | POST   | DormSaveDTO  | Result<DormVO>       | 新增寮                       |
| updateDorm  | /api/dorm/{id} | PUT    | DormSaveDTO  | Result<DormVO>       | 更新寮信息                   |
| deleteDorm  | /api/dorm/{id} | DELETE | —            | Result<Boolean>      | 逻辑删除寮                   |

### 2.2 EmployeeController `/api/employee`

| 方法            | 路径               | HTTP   | 入参             | 返回                     | 说明                         |
| --------------- | ------------------ | ------ | ---------------- | ------------------------ | ---------------------------- |
| getEmployeeList | /api/employee/list | GET    | EmployeeQueryDTO | Result<List<EmployeeVO>> | 分页查询社員列表             |
| addEmployee     | /api/employee      | POST   | EmployeeSaveDTO  | Result<EmployeeVO>       | 新增社員（含工号唯一性校验） |
| updateEmployee  | /api/employee/{id} | PUT    | EmployeeSaveDTO  | Result<EmployeeVO>       | 更新社員信息                 |
| deleteEmployee  | /api/employee/{id} | DELETE | —                | Result<Boolean>          | 逻辑删除社員                 |

### 2.3 OccupancyController `/api/occupancy`

| 方法             | 路径                         | HTTP | 入参              | 返回                      | 说明                                            |
| ---------------- | ---------------------------- | ---- | ----------------- | ------------------------- | ----------------------------------------------- |
| getOccupancyList | /api/occupancy/list          | GET  | OccupancyQueryDTO | Result<List<OccupancyVO>> | 分页查询入退寮记录                              |
| addOccupancy     | /api/occupancy               | POST | CheckInDTO        | Result<OccupancyVO>       | 入寮登録（校验部屋状态，更新 room_info.status） |
| checkOut         | /api/occupancy/{id}/checkout | PUT  | CheckOutDTO       | Result<Boolean>           | 退寮処理（更新 room_info.status 为 vacant）     |

### 2.4 VacancyController `/api/vacancy`

| 方法           | 路径              | HTTP | 入参            | 返回                    | 说明                             |
| -------------- | ----------------- | ---- | --------------- | ----------------------- | -------------------------------- |
| getVacancyList | /api/vacancy/list | GET  | VacancyQueryDTO | Result<List<VacancyVO>> | 分页查询空き室列表（关联寮信息） |

### 2.5 FeeController `/api/fee`

| 方法           | 路径                  | HTTP | 入参             | 返回                | 说明                             |
| -------------- | --------------------- | ---- | ---------------- | ------------------- | -------------------------------- |
| getFeeList     | /api/fee/list         | GET  | year, month      | Result<List<FeeVO>> | 按年月查询寮費列表               |
| calculateFees  | /api/fee/calculate    | POST | FeeConfirmAllDTO | Result<List<FeeVO>> | 重新计算指定月份寮費（先删后插） |
| confirmFee     | /api/fee/{id}/confirm | PUT  | —                | Result<Boolean>     | 单件确定寮費                     |
| confirmAllFees | /api/fee/confirm-all  | PUT  | FeeConfirmAllDTO | Result<Boolean>     | 全件确定指定月份寮費             |

### 2.6 DashboardController `/api/dashboard`

| 方法              | 路径                 | HTTP | 入参 | 返回                | 说明                                    |
| ----------------- | -------------------- | ---- | ---- | ------------------- | --------------------------------------- |
| getDashboardStats | /api/dashboard/stats | GET  | —    | Result<DashboardVO> | 获取仪表盘统计数据（Redis 缓存 5 分钟） |

---

## 三、Service 层（业务方法清单）

### 3.1 DormService / DormServiceImpl

| 方法                          | 说明                           | 事务 | 缓存     |
| ----------------------------- | ------------------------------ | ---- | -------- |
| getDormList(DormQueryDTO)     | 分页查询                       | 无   | 无       |
| addDorm(DormSaveDTO)          | 新增寮，清除 DORM_LIST 缓存    | 无   | 写后删除 |
| updateDorm(Long, DormSaveDTO) | 更新寮，校验存在性，清除缓存   | 无   | 写后删除 |
| deleteDorm(Long)              | 逻辑删除，校验存在性，清除缓存 | 无   | 写后删除 |

### 3.2 EmployeeService / EmployeeServiceImpl

| 方法                                  | 说明                    | 事务 |
| ------------------------------------- | ----------------------- | ---- |
| getEmployeeList(EmployeeQueryDTO)     | 分页查询                | 无   |
| addEmployee(EmployeeSaveDTO)          | 新增，校验 empId 唯一性 | 无   |
| updateEmployee(Long, EmployeeSaveDTO) | 更新，校验存在性        | 无   |
| deleteEmployee(Long)                  | 逻辑删除，校验存在性    | 无   |

### 3.3 OccupancyService / OccupancyServiceImpl

| 方法                                | 说明                                                    | 事务           |
| ----------------------------------- | ------------------------------------------------------- | -------------- |
| getOccupancyList(OccupancyQueryDTO) | 分页查询                                                | 无             |
| addOccupancy(CheckInDTO)            | 入寮，校验社員/部屋/状态，联动更新 room status=occupied | @Transactional |
| checkOut(Long, CheckOutDTO)         | 退寮，校验记录/状态，联动更新 room status=vacant        | @Transactional |

### 3.4 VacancyService / VacancyServiceImpl

| 方法                            | 说明                                        | 事务 |
| ------------------------------- | ------------------------------------------- | ---- |
| getVacancyList(VacancyQueryDTO) | 分页查询，关联 dorm_info 取寮类型/地区/户型 | 无   |

### 3.5 FeeService / FeeServiceImpl

| 方法                             | 说明                                                              | 事务           |
| -------------------------------- | ----------------------------------------------------------------- | -------------- |
| getFeeList(Integer, Integer)     | 按年月查询                                                        | 无             |
| calculateFees(Integer, Integer)  | 删除旧记录，遍历活跃入居计算 `area×2000×days/totalDays`，批量插入 | @Transactional |
| confirmFee(Long)                 | 单件确定，校验状态                                                | 无             |
| confirmAllFees(Integer, Integer) | 全件确定                                                          | 无             |

### 3.6 DashboardService / DashboardServiceImpl

| 方法                | 说明                                                                             | 缓存           |
| ------------------- | -------------------------------------------------------------------------------- | -------------- |
| getDashboardStats() | 汇总：整体入居率、各寮饼图、近6月柱状图、近6月折线图、长期入居警告、最新10条记录 | Redis TTL=5min |

---

## 四、Mapper 层（接口 + SQL 说明）

### 4.1 DormMapper → dorm_info

| 方法                         | SQL 类型                                | 说明                                |
| ---------------------------- | --------------------------------------- | ----------------------------------- |
| selectDormList(DormQueryDTO) | SELECT + LEFT JOIN room_info + GROUP BY | 含 occupied_rooms / room_count 聚合 |
| countDormList(DormQueryDTO)  | SELECT COUNT(DISTINCT d.id)             | 分页用总数                          |
| selectById(Long)             | SELECT                                  | 按主键查询                          |
| insertDorm(DormInfo)         | INSERT                                  | 新增                                |
| updateDorm(DormInfo)         | UPDATE \<set\>                          | 动态更新                            |
| deleteDormById(Long)         | UPDATE is_deleted=1                     | 逻辑删除                            |

### 4.2 RoomMapper → room_info

| 方法                               | SQL 类型                     | 说明                       |
| ---------------------------------- | ---------------------------- | -------------------------- |
| selectVacancyList(VacancyQueryDTO) | SELECT + LEFT JOIN dorm_info | 含 dorm_type/region/layout |
| countVacancyList(VacancyQueryDTO)  | SELECT COUNT                 | 分页用总数                 |
| selectById(Long)                   | SELECT                       | 按主键查询                 |
| updateRoomStatus(Long, String)     | UPDATE status                | 入退寮时联动更新           |
| countOccupiedByDormId(Long)        | SELECT COUNT                 | 统计某寮入居数             |
| countRoomsByDormId(Long)           | SELECT COUNT                 | 统计某寮总部屋数           |
| countTotalRooms()                  | SELECT COUNT                 | 全体总部屋数（仪表盘）     |
| countTotalOccupied()               | SELECT COUNT                 | 全体入居部屋数（仪表盘）   |

### 4.3 EmployeeMapper → employee_info

| 方法                                 | SQL 类型            | 说明                     |
| ------------------------------------ | ------------------- | ------------------------ |
| selectEmployeeList(EmployeeQueryDTO) | SELECT              | 动态条件分页             |
| countEmployeeList(EmployeeQueryDTO)  | SELECT COUNT        | 分页总数                 |
| selectById(Long)                     | SELECT              | 按主键                   |
| selectByEmpId(String)                | SELECT              | 按工号查询，唯一性校验用 |
| insertEmployee(EmployeeInfo)         | INSERT              | 日本/中国社員通用        |
| updateEmployee(EmployeeInfo)         | UPDATE \<set\>      | 动态更新                 |
| deleteEmployeeById(Long)             | UPDATE is_deleted=1 | 逻辑删除                 |

### 4.4 OccupancyMapper → occupancy_record

| 方法                                   | SQL 类型                         | 说明                           |
| -------------------------------------- | -------------------------------- | ------------------------------ |
| selectOccupancyList(OccupancyQueryDTO) | SELECT                           | 动态条件分页，ORDER BY id DESC |
| countOccupancyList(OccupancyQueryDTO)  | SELECT COUNT                     | 分页总数                       |
| selectById(Long)                       | SELECT                           | 按主键                         |
| insertOccupancy(OccupancyRecord)       | INSERT                           | 新增入寮记录                   |
| checkOut(Long, CheckOutDTO)            | UPDATE                           | 更新退寮日期/事由/状态         |
| selectRecentOccupancy(Integer)         | SELECT LIMIT                     | 最新N条，仪表盘用              |
| selectActiveOccupancyList()            | SELECT WHERE status=active       | 当前入居中，寮費计算用         |
| selectLongTermWarnings()               | SELECT + LEFT JOIN employee_info | 入居超5年，含 dept 字段        |
| selectMonthlyStats(Integer)            | UNION ALL 子查询                 | 近N月入退寮件数                |
| selectMonthlyOccupancyRate(int)        | 子查询 LAST_DAY + LEFT JOIN      | 近N月每月末入居率（%）         |

### 4.5 FeeMapper → fee_record

| 方法                                   | SQL 类型                | 说明           |
| -------------------------------------- | ----------------------- | -------------- |
| selectFeeList(Integer, Integer)        | SELECT WHERE year+month | 按年月查询     |
| selectById(Long)                       | SELECT                  | 按主键         |
| insertFeeBatch(List)                   | INSERT \<foreach\>      | 批量插入       |
| deleteFeeByYearMonth(Integer, Integer) | DELETE（物理删除）      | 重新计算前清空 |
| confirmFeeById(Long)                   | UPDATE status=confirmed | 单件确定       |
| confirmAllFees(Integer, Integer)       | UPDATE WHERE year+month | 全件确定       |

---

## 五、Entity 层

| 实体类          | 对应表           | 关键字段                                                                                                                                 |
| --------------- | ---------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| DormInfo        | dorm_info        | id, name, address, type, layout, region, totalRooms, status, isDeleted                                                                   |
| RoomInfo        | room_info        | id, dormId, dormName, name, area(BigDecimal), status, gender, isDeleted                                                                  |
| EmployeeInfo    | employee_info    | id, empId, name, type, gender; jp字段: dept/division/empType/mobile/email/firstCheckIn; cn字段: origin/bizType/arrivalDate/departureDate |
| OccupancyRecord | occupancy_record | id, empId, empName, empType, dormId, dormName, roomId, roomName, checkInDate, checkOutDate, status, reason                               |
| FeeRecord       | fee_record       | id, year, month, empId, empName, empDept, dormName, roomName, area, days, totalDays, unitPrice, amount, status                           |

---

## 六、DTO 层

| 类名              | 用途                                  | 校验注解                                                         |
| ----------------- | ------------------------------------- | ---------------------------------------------------------------- |
| DormQueryDTO      | 寮列表查询（含 page/pageSize/offset） | 无                                                               |
| DormSaveDTO       | 寮新增/编辑                           | @NotBlank(name,address,type,region) @NotNull(totalRooms) @Min(1) |
| EmployeeQueryDTO  | 社員列表查询                          | 无                                                               |
| EmployeeSaveDTO   | 社員新增/编辑                         | @NotBlank(empId,name,type)                                       |
| CheckInDTO        | 入寮登録                              | @NotNull(empId,roomId,checkInDate)                               |
| CheckOutDTO       | 退寮登録                              | @NotNull(checkOutDate)                                           |
| VacancyQueryDTO   | 空き室列表查询                        | 无                                                               |
| FeeConfirmAllDTO  | 寮費全件确定/计算                     | @NotNull @Min/@Max(year,month)                                   |
| OccupancyQueryDTO | 入退寮列表查询                        | 无                                                               |

---

## 七、VO 层

| 类名        | 说明                                                                                |
| ----------- | ----------------------------------------------------------------------------------- |
| DormVO      | 寮信息 + occupiedRooms + roomCount                                                  |
| EmployeeVO  | 社員全字段（jp+cn）                                                                 |
| OccupancyVO | 入退寮记录                                                                          |
| VacancyVO   | 空き室 + dormType/dormRegion/dormLayout                                             |
| FeeVO       | 寮費记录全字段                                                                      |
| DashboardVO | 仪表盘聚合 VO（含4个内部类：DormStatVO/MonthlyDataVO/RateDataVO/LongTermWarningVO） |

---

## 八、常量层

### CommonConstants

| 常量                                | 值                 | 说明              |
| ----------------------------------- | ------------------ | ----------------- |
| DORM_TYPE_MALE/FEMALE/MIXED         | male/female/mixed  | 寮类型            |
| EMP_TYPE_JP/CN                      | jp/cn              | 社員类型          |
| EMP_ORIGIN_SHENYANG/DALIAN          | shenyang/dalian    | 中国社員来源地    |
| EMP_DEPT_SHENYANG/DALIAN            | 瀋陽/大連          | 寮費部门显示名    |
| ROOM_STATUS_VACANT/OCCUPIED         | vacant/occupied    | 部屋状态          |
| OCCUPANCY_STATUS_ACTIVE/CHECKED_OUT | active/checked_out | 入居状态          |
| FEE_STATUS_PENDING/CONFIRMED        | pending/confirmed  | 寮費状态          |
| FEE_UNIT_PRICE                      | 2000               | 寮費单价（円/㎡） |
| MSG\_\*                             | 各类提示语         | 全部错误/成功提示 |

### RedisKeyConstants

| Key             | 值                  | TTL                      |
| --------------- | ------------------- | ------------------------ |
| DORM_LIST       | dms:dorm:list       | 写后删除                 |
| DASHBOARD_STATS | dms:dashboard:stats | 5分钟（CACHE_TTL_SHORT） |

---

## 九、调用链路

### 查询寮列表

```
GET /api/dorm/list
    → DormController.getDormList(DormQueryDTO)
    → DormServiceImpl.getDormList() [offset = (page-1)*pageSize]
    → DormMapper.selectDormList() / countDormList()
    → DormMapper.xml：LEFT JOIN room_info GROUP BY
    → Result<List<DormVO>>
```

### 入寮登録

```
POST /api/occupancy
    → OccupancyController.addOccupancy(@Valid CheckInDTO)
    → OccupancyServiceImpl.addOccupancy() [@Transactional]
        → EmployeeMapper.selectById()  [校验社員存在]
        → RoomMapper.selectById()      [校验部屋存在+状态为vacant]
        → OccupancyMapper.insertOccupancy()
        → RoomMapper.updateRoomStatus(roomId, "occupied")
    → Result<OccupancyVO>
```

### 退寮処理

```
PUT /api/occupancy/{id}/checkout
    → OccupancyController.checkOut(@Valid CheckOutDTO)
    → OccupancyServiceImpl.checkOut() [@Transactional]
        → OccupancyMapper.selectById()  [校验记录存在+状态为active]
        → OccupancyMapper.checkOut()
        → RoomMapper.updateRoomStatus(roomId, "vacant")
    → Result<Boolean>
```

### 计算寮費

```
POST /api/fee/calculate
    → FeeController.calculateFees(@Valid FeeConfirmAllDTO)
    → FeeServiceImpl.calculateFees() [@Transactional]
        → FeeMapper.deleteFeeByYearMonth()  [清空旧记录]
        → OccupancyMapper.selectActiveOccupancyList()
        → (foreach) RoomMapper.selectById() / EmployeeMapper.selectById()
        → amount = ROUND(area × 2000 × days / totalDays)
        → FeeMapper.insertFeeBatch(feeList)
        → FeeMapper.selectFeeList()
    → Result<List<FeeVO>>
```

### 获取仪表盘

```
GET /api/dashboard/stats
    → DashboardController.getDashboardStats()
    → DashboardServiceImpl.getDashboardStats() [Redis 5min 缓存]
        → RoomMapper.countTotalRooms() / countTotalOccupied()
        → DormMapper.selectDormList()
        → RoomMapper.countOccupiedByDormId() / countRoomsByDormId()
        → OccupancyMapper.selectRecentOccupancy(10)
        → OccupancyMapper.selectMonthlyStats(6)
        → OccupancyMapper.selectMonthlyOccupancyRate(6)
        → OccupancyMapper.selectLongTermWarnings()
    → Result<DashboardVO>
```
