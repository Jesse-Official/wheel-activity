# 題目
- 設計一個電商轉盤抽獎功能
 - 3種獎品各有N種數量
 - 每個獎品的中獎機率不同
 - 與銘謝惠顧合起來機率為100%
 - 可有同時多次抽獎的機會
 - 防止重複抽獎的情況
 - 防止獎品超抽的情況

# 實作

## 流程設計
- 抽獎流程:
    - 檢查用戶是否在短時間內重複抽獎
    - 檢查用戶剩餘抽獎次數
    - 從 Repository 取得活動資訊
    - 執行抽獎邏輯，根據機率與庫存決定結果
    - 抽獎時即時從 Redis 扣減庫存，防止超抽，若庫存不足以視為未中獎
    - 返回抽獎結果
- 庫存同步:
    - 定期(10秒)將 Redis 的庫存同步回 WheelActivity 實體，確保資料一致性

## 技術架構
- 使用 Spring Boot Web 框架實現 RESTful API
- 以 Redis 作為活動與獎品庫存的快取與同步機制（可切換為 H2/JPA 方案，方便本地開發與測試）
- 主要分層如下：
    - Domain Layer：包含 WheelActivity、Prize、DrawResult 等領域模型
    - Repository Layer：WheelActivityRepository，負責活動與獎品資料的持久化（可選 Redis 或 JPA/H2）
    - Application Layer：WheelApplicationService，負責抽獎流程與業務邏輯
    - Service Layer：ActivityService，封裝抽獎演算法與業務規則
    - Infrastructure Layer：提供 Redis、JPA 等實際資料存取實作

## 測試方式
- 啟動專案後，可用 Postman、curl 或 test.http 檔案進行 API 測試。
- 範例：

```sh
curl -X POST \
  http://localhost:8080/api/wheel/play \
  -H "Content-Type: application/json" \
  -d '{
    "activityId": 1,
    "userId": 1000,
    "drawTimes": 10
  }'
```

- 可透過 `/h2-console` 進入 H2 資料庫檢查資料。



