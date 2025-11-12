---
title: Default module
language_tabs:
  - shell: Shell
  - http: HTTP
  - javascript: JavaScript
  - ruby: Ruby
  - python: Python
  - php: PHP
  - java: Java
  - go: Go
toc_footers: []
includes: []
search: true
code_clipboard: true
highlight_theme: darkula
headingLevel: 2
generator: "@tarslib/widdershins v4.0.30"

---

# Default module

ELM API FOR DEVELOPERS

Base URLs:

# Authentication

* API Key (token)
    - Parameter Name: **token**, in: header. 

# 虚拟钱包

<a id="opIdwithdrawal"></a>

## GET 提现

GET /api/wallet/withdrawal

### Params

|Name|Location|Type|Required|Description|
|---|---|---|---|---|
|amount|query|number| yes |none|
|Authorization|header|string| no |none|

> Response Examples

> 200 Response

```
{"success":true,"code":"string","data":true,"message":"string"}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[HttpResultBoolean](#schemahttpresultboolean)|

<a id="opIdwalletVipRule"></a>

## GET 获取虚拟钱包vip规则

GET /api/wallet/vip/rule

### Params

|Name|Location|Type|Required|Description|
|---|---|---|---|---|
|Authorization|header|string| no |none|

> Response Examples

> 200 Response

```
{"success":true,"code":"string","data":[{"id":0,"name":"string","description":"string","cost":0}],"message":"string"}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[HttpResultListWalletVipVO](#schemahttpresultlistwalletvipvo)|

<a id="opIdwalletVipRule_1"></a>

## GET 申请vip

GET /api/wallet/vip/apply

### Params

|Name|Location|Type|Required|Description|
|---|---|---|---|---|
|vipLevel|query|integer(int32)| yes |none|
|Authorization|header|string| no |none|

> Response Examples

> 200 Response

```
{"success":true,"code":"string","data":true,"message":"string"}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[HttpResultBoolean](#schemahttpresultboolean)|

<a id="opIdpay"></a>

## GET 使用钱包支付订单

GET /api/wallet/transaction/payment

注：订单确认前金额不会到商家钱包中

### Params

|Name|Location|Type|Required|Description|
|---|---|---|---|---|
|orderId|query|integer(int64)| yes |none|
|Authorization|header|string| no |none|

> Response Examples

> 200 Response

```
{"success":true,"code":"string","data":true,"message":"string"}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[HttpResultBoolean](#schemahttpresultboolean)|

<a id="opIdtransactionList"></a>

## GET 获取用户钱包明细列表

GET /api/wallet/transaction/list

### Params

|Name|Location|Type|Required|Description|
|---|---|---|---|---|
|type|query|integer(int32)| no |none|
|status|query|integer(int32)| no |none|
|startDate|query|string(date)| no |none|
|endDate|query|string(date)| no |none|
|Authorization|header|string| no |none|

> Response Examples

> 200 Response

```
{"success":true,"code":"string","data":[{"id":0,"type":0,"amount":0,"fee":0,"createTime":"2019-08-24T14:15:22Z","inOrOut":0}],"message":"string"}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[HttpResultListTransactionRecordVO](#schemahttpresultlisttransactionrecordvo)|

<a id="opIdtransactionDetail"></a>

## GET 根据明细id获取指定明细详细信息

GET /api/wallet/transaction/detail

### Params

|Name|Location|Type|Required|Description|
|---|---|---|---|---|
|transactionId|query|integer(int64)| yes |none|
|Authorization|header|string| no |none|

> Response Examples

> 200 Response

```
{"success":true,"code":"string","data":{"id":0,"type":0,"amount":0,"fee":0,"createTime":"2019-08-24T14:15:22Z","inOrOut":0,"status":0,"from_account":0,"to_account":0,"from_account_name":"string","to_account_name":"string","fee_rate":0.1},"message":"string"}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[HttpResultTransactionRecordDetailVO](#schemahttpresulttransactionrecorddetailvo)|

<a id="opIdtransactionDetailByOrder"></a>

## GET 根据订单id获取指定明细详细信息

GET /api/wallet/transaction/detail/order

### Params

|Name|Location|Type|Required|Description|
|---|---|---|---|---|
|orderId|query|integer(int64)| yes |none|
|Authorization|header|string| no |none|

> Response Examples

> 200 Response

```
{"success":true,"code":"string","data":{"id":0,"type":0,"amount":0,"fee":0,"createTime":"2019-08-24T14:15:22Z","inOrOut":0,"status":0,"from_account":0,"to_account":0,"from_account_name":"string","to_account_name":"string","fee_rate":0.1},"message":"string"}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[HttpResultTransactionRecordDetailVO](#schemahttpresulttransactionrecorddetailvo)|

<a id="opIdwalletRule"></a>

## GET 获取虚拟钱包手续费&奖励规则

GET /api/wallet/rule

### Params

|Name|Location|Type|Required|Description|
|---|---|---|---|---|
|Authorization|header|string| no |none|

> Response Examples

> 200 Response

```
{"success":true,"code":"string","data":"string","message":"string"}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[HttpResultString](#schemahttpresultstring)|

<a id="opIdrecharge"></a>

## GET 充值

GET /api/wallet/recharge

### Params

|Name|Location|Type|Required|Description|
|---|---|---|---|---|
|amount|query|number| yes |none|
|Authorization|header|string| no |none|

> Response Examples

> 200 Response

```
{"success":true,"code":"string","data":true,"message":"string"}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[HttpResultBoolean](#schemahttpresultboolean)|

<a id="opIdpreview"></a>

## GET 提现/充值预览

GET /api/wallet/preview

返回手续费/奖励金额,option 0-充值 1-提现

### Params

|Name|Location|Type|Required|Description|
|---|---|---|---|---|
|amount|query|number| yes |none|
|option|query|integer(int32)| yes |none|
|Authorization|header|string| no |none|

> Response Examples

> 200 Response

```
{"success":true,"code":"string","data":{"amount":0,"fee":0,"total":0,"fee_rate":0.1,"isOver":true},"message":"string"}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[HttpResultPreviewVO](#schemahttpresultpreviewvo)|

<a id="opIdopenWallet"></a>

## GET 用户开通钱包

GET /api/wallet/open

### Params

|Name|Location|Type|Required|Description|
|---|---|---|---|---|
|Authorization|header|string| no |none|

> Response Examples

> 200 Response

```
{"success":true,"code":"string","data":0,"message":"string"}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[HttpResultLong](#schemahttpresultlong)|

<a id="opIdwalletMessage"></a>

## GET 获取用户钱包信息

GET /api/wallet/message

未开通会抛异常

### Params

|Name|Location|Type|Required|Description|
|---|---|---|---|---|
|Authorization|header|string| no |none|

> Response Examples

> 200 Response

```
{"success":true,"code":"string","data":{"id":0,"userId":0,"createTime":"2019-08-24T14:15:22Z","status":0,"vipLevel":0,"balance":0,"overdraftAmount":0,"overdrawnAmount":0,"username":"string","vipName":"string","vipDescription":"string","overdraftLimit":0},"message":"string"}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[HttpResultWalletVO](#schemahttpresultwalletvo)|

<a id="opIdrepayLoan"></a>

## GET 还贷款

GET /api/wallet/loan/repay

### Params

|Name|Location|Type|Required|Description|
|---|---|---|---|---|
|id|query|integer(int64)| yes |none|
|Authorization|header|string| no |none|

> Response Examples

> 200 Response

```
{"success":true,"code":"string","data":true,"message":"string"}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[HttpResultBoolean](#schemahttpresultboolean)|

<a id="opIdlistLoan"></a>

## GET 查看钱包贷款

GET /api/wallet/loan/list

LocalDateTime不为null就是还了的

### Params

|Name|Location|Type|Required|Description|
|---|---|---|---|---|
|Authorization|header|string| no |none|

> Response Examples

> 200 Response

```
{"success":true,"code":"string","data":[{"id":0,"walletId":0,"createTime":"2019-08-24T14:15:22Z","repayTime":"2019-08-24T14:15:22Z","loanAmount":0}],"message":"string"}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[HttpResultListVirtualWalletLoan](#schemahttpresultlistvirtualwalletloan)|

# Data Schema

<h2 id="tocS_HttpResultString">HttpResultString</h2>

<a id="schemahttpresultstring"></a>
<a id="schema_HttpResultString"></a>
<a id="tocShttpresultstring"></a>
<a id="tocshttpresultstring"></a>

```json
{
  "success": true,
  "code": "string",
  "data": "string",
  "message": "string"
}

```

### Attribute

|Name|Type|Required|Restrictions|Title|Description|
|---|---|---|---|---|---|
|success|boolean|false|none||none|
|code|string|false|none||none|
|data|string|false|none||none|
|message|string|false|none||none|

<h2 id="tocS_HttpResultLong">HttpResultLong</h2>

<a id="schemahttpresultlong"></a>
<a id="schema_HttpResultLong"></a>
<a id="tocShttpresultlong"></a>
<a id="tocshttpresultlong"></a>

```json
{
  "success": true,
  "code": "string",
  "data": 0,
  "message": "string"
}

```

### Attribute

|Name|Type|Required|Restrictions|Title|Description|
|---|---|---|---|---|---|
|success|boolean|false|none||none|
|code|string|false|none||none|
|data|integer(int64)|false|none||none|
|message|string|false|none||none|

<h2 id="tocS_HttpResultBoolean">HttpResultBoolean</h2>

<a id="schemahttpresultboolean"></a>
<a id="schema_HttpResultBoolean"></a>
<a id="tocShttpresultboolean"></a>
<a id="tocshttpresultboolean"></a>

```json
{
  "success": true,
  "code": "string",
  "data": true,
  "message": "string"
}

```

### Attribute

|Name|Type|Required|Restrictions|Title|Description|
|---|---|---|---|---|---|
|success|boolean|false|none||none|
|code|string|false|none||none|
|data|boolean|false|none||none|
|message|string|false|none||none|

<h2 id="tocS_HttpResultListWalletVipVO">HttpResultListWalletVipVO</h2>

<a id="schemahttpresultlistwalletvipvo"></a>
<a id="schema_HttpResultListWalletVipVO"></a>
<a id="tocShttpresultlistwalletvipvo"></a>
<a id="tocshttpresultlistwalletvipvo"></a>

```json
{
  "success": true,
  "code": "string",
  "data": [
    {
      "id": 0,
      "name": "string",
      "description": "string",
      "cost": 0
    }
  ],
  "message": "string"
}

```

### Attribute

|Name|Type|Required|Restrictions|Title|Description|
|---|---|---|---|---|---|
|success|boolean|false|none||none|
|code|string|false|none||none|
|data|[[WalletVipVO](#schemawalletvipvo)]|false|none||none|
|message|string|false|none||none|

<h2 id="tocS_WalletVipVO">WalletVipVO</h2>

<a id="schemawalletvipvo"></a>
<a id="schema_WalletVipVO"></a>
<a id="tocSwalletvipvo"></a>
<a id="tocswalletvipvo"></a>

```json
{
  "id": 0,
  "name": "string",
  "description": "string",
  "cost": 0
}

```

### Attribute

|Name|Type|Required|Restrictions|Title|Description|
|---|---|---|---|---|---|
|id|integer(int32)|false|none||none|
|name|string|false|none||vip名称|
|description|string|false|none||描述|
|cost|number|false|none||申请费用/月|

<h2 id="tocS_HttpResultListTransactionRecordVO">HttpResultListTransactionRecordVO</h2>

<a id="schemahttpresultlisttransactionrecordvo"></a>
<a id="schema_HttpResultListTransactionRecordVO"></a>
<a id="tocShttpresultlisttransactionrecordvo"></a>
<a id="tocshttpresultlisttransactionrecordvo"></a>

```json
{
  "success": true,
  "code": "string",
  "data": [
    {
      "id": 0,
      "type": 0,
      "amount": 0,
      "fee": 0,
      "createTime": "2019-08-24T14:15:22Z",
      "inOrOut": 0
    }
  ],
  "message": "string"
}

```

### Attribute

|Name|Type|Required|Restrictions|Title|Description|
|---|---|---|---|---|---|
|success|boolean|false|none||none|
|code|string|false|none||none|
|data|[[TransactionRecordVO](#schematransactionrecordvo)]|false|none||none|
|message|string|false|none||none|

<h2 id="tocS_TransactionRecordVO">TransactionRecordVO</h2>

<a id="schematransactionrecordvo"></a>
<a id="schema_TransactionRecordVO"></a>
<a id="tocStransactionrecordvo"></a>
<a id="tocstransactionrecordvo"></a>

```json
{
  "id": 0,
  "type": 0,
  "amount": 0,
  "fee": 0,
  "createTime": "2019-08-24T14:15:22Z",
  "inOrOut": 0
}

```

### Attribute

|Name|Type|Required|Restrictions|Title|Description|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||交易id|
|type|integer(int32)|false|none||交易类型 0-支付 1-收款 2-提现 3-充值|
|amount|number|false|none||操作金额|
|fee|number|false|none||手续费或奖励|
|createTime|string(date-time)|false|none||交易时间|
|inOrOut|integer(int32)|false|none||支出还是收入,0-支出 1-收入|

<h2 id="tocS_HttpResultTransactionRecordDetailVO">HttpResultTransactionRecordDetailVO</h2>

<a id="schemahttpresulttransactionrecorddetailvo"></a>
<a id="schema_HttpResultTransactionRecordDetailVO"></a>
<a id="tocShttpresulttransactionrecorddetailvo"></a>
<a id="tocshttpresulttransactionrecorddetailvo"></a>

```json
{
  "success": true,
  "code": "string",
  "data": {
    "id": 0,
    "type": 0,
    "amount": 0,
    "fee": 0,
    "createTime": "2019-08-24T14:15:22Z",
    "inOrOut": 0,
    "status": 0,
    "from_account": 0,
    "to_account": 0,
    "from_account_name": "string",
    "to_account_name": "string",
    "fee_rate": 0.1
  },
  "message": "string"
}

```

### Attribute

|Name|Type|Required|Restrictions|Title|Description|
|---|---|---|---|---|---|
|success|boolean|false|none||none|
|code|string|false|none||none|
|data|[TransactionRecordDetailVO](#schematransactionrecorddetailvo)|false|none||none|
|message|string|false|none||none|

<h2 id="tocS_TransactionRecordDetailVO">TransactionRecordDetailVO</h2>

<a id="schematransactionrecorddetailvo"></a>
<a id="schema_TransactionRecordDetailVO"></a>
<a id="tocStransactionrecorddetailvo"></a>
<a id="tocstransactionrecorddetailvo"></a>

```json
{
  "id": 0,
  "type": 0,
  "amount": 0,
  "fee": 0,
  "createTime": "2019-08-24T14:15:22Z",
  "inOrOut": 0,
  "status": 0,
  "from_account": 0,
  "to_account": 0,
  "from_account_name": "string",
  "to_account_name": "string",
  "fee_rate": 0.1
}

```

### Attribute

|Name|Type|Required|Restrictions|Title|Description|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||交易id|
|type|integer(int32)|false|none||交易类型 0-支付 1-收款 2-提现 3-充值|
|amount|number|false|none||操作金额|
|fee|number|false|none||手续费或奖励|
|createTime|string(date-time)|false|none||交易时间|
|inOrOut|integer(int32)|false|none||支出还是收入,0-支出 1-收入|
|status|integer(int32)|false|none||操作金额是否为冻结 0-否 1-是|
|from_account|integer(int64)|false|none||转出钱包 交易类型为充值时值为0|
|to_account|integer(int64)|false|none||转入钱包 交易类型为提现时值为0|
|from_account_name|string|false|none||转出钱包用户姓名 交易类型为充值时值为null|
|to_account_name|string|false|none||转入钱包用户姓名 交易类型为提现时值为null|
|fee_rate|number(float)|false|none||手续费率或奖励率|

<h2 id="tocS_HttpResultWalletVO">HttpResultWalletVO</h2>

<a id="schemahttpresultwalletvo"></a>
<a id="schema_HttpResultWalletVO"></a>
<a id="tocShttpresultwalletvo"></a>
<a id="tocshttpresultwalletvo"></a>

```json
{
  "success": true,
  "code": "string",
  "data": {
    "id": 0,
    "userId": 0,
    "createTime": "2019-08-24T14:15:22Z",
    "status": 0,
    "vipLevel": 0,
    "balance": 0,
    "overdraftAmount": 0,
    "overdrawnAmount": 0,
    "username": "string",
    "vipName": "string",
    "vipDescription": "string",
    "overdraftLimit": 0
  },
  "message": "string"
}

```

### Attribute

|Name|Type|Required|Restrictions|Title|Description|
|---|---|---|---|---|---|
|success|boolean|false|none||none|
|code|string|false|none||none|
|data|[WalletVO](#schemawalletvo)|false|none||none|
|message|string|false|none||none|

<h2 id="tocS_HttpResultPreviewVO">HttpResultPreviewVO</h2>

<a id="schemahttpresultpreviewvo"></a>
<a id="schema_HttpResultPreviewVO"></a>
<a id="tocShttpresultpreviewvo"></a>
<a id="tocshttpresultpreviewvo"></a>

```json
{
  "success": true,
  "code": "string",
  "data": {
    "amount": 0,
    "fee": 0,
    "total": 0,
    "fee_rate": 0.1,
    "isOver": true
  },
  "message": "string"
}

```

### Attribute

|Name|Type|Required|Restrictions|Title|Description|
|---|---|---|---|---|---|
|success|boolean|false|none||none|
|code|string|false|none||none|
|data|[PreviewVO](#schemapreviewvo)|false|none||none|
|message|string|false|none||none|

<h2 id="tocS_WalletVO">WalletVO</h2>

<a id="schemawalletvo"></a>
<a id="schema_WalletVO"></a>
<a id="tocSwalletvo"></a>
<a id="tocswalletvo"></a>

```json
{
  "id": 0,
  "userId": 0,
  "createTime": "2019-08-24T14:15:22Z",
  "status": 0,
  "vipLevel": 0,
  "balance": 0,
  "overdraftAmount": 0,
  "overdrawnAmount": 0,
  "username": "string",
  "vipName": "string",
  "vipDescription": "string",
  "overdraftLimit": 0
}

```

### Attribute

|Name|Type|Required|Restrictions|Title|Description|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||虚拟钱包id|
|userId|integer(int64)|false|none||所属用户id|
|createTime|string(date-time)|false|none||创建时间|
|status|integer(int32)|false|none||钱包状态 0-正常 1-冻结|
|vipLevel|integer(int32)|false|none||vip级别 0-非vip|
|balance|number|false|none||余额|
|overdraftAmount|number|false|none||可透支金额|
|overdrawnAmount|number|false|none||已透支金额|
|username|string|false|none||所属用户名|
|vipName|string|false|none||vip名|
|vipDescription|string|false|none||vip描述|
|overdraftLimit|number|false|none||可透支金额|

<h2 id="tocS_PreviewVO">PreviewVO</h2>

<a id="schemapreviewvo"></a>
<a id="schema_PreviewVO"></a>
<a id="tocSpreviewvo"></a>
<a id="tocspreviewvo"></a>

```json
{
  "amount": 0,
  "fee": 0,
  "total": 0,
  "fee_rate": 0.1,
  "isOver": true
}

```

### Attribute

|Name|Type|Required|Restrictions|Title|Description|
|---|---|---|---|---|---|
|amount|number|false|none||操作金额|
|fee|number|false|none||手续费或奖励|
|total|number|false|none||总金额|
|fee_rate|number(float)|false|none||手续费率或奖励率|
|isOver|boolean|false|none||对于提现，是否透支，无法提现超过余额的金额|

<h2 id="tocS_HttpResultListVirtualWalletLoan">HttpResultListVirtualWalletLoan</h2>

<a id="schemahttpresultlistvirtualwalletloan"></a>
<a id="schema_HttpResultListVirtualWalletLoan"></a>
<a id="tocShttpresultlistvirtualwalletloan"></a>
<a id="tocshttpresultlistvirtualwalletloan"></a>

```json
{
  "success": true,
  "code": "string",
  "data": [
    {
      "id": 0,
      "walletId": 0,
      "createTime": "2019-08-24T14:15:22Z",
      "repayTime": "2019-08-24T14:15:22Z",
      "loanAmount": 0
    }
  ],
  "message": "string"
}

```

### Attribute

|Name|Type|Required|Restrictions|Title|Description|
|---|---|---|---|---|---|
|success|boolean|false|none||none|
|code|string|false|none||none|
|data|[[VirtualWalletLoan](#schemavirtualwalletloan)]|false|none||none|
|message|string|false|none||none|

<h2 id="tocS_VirtualWalletLoan">VirtualWalletLoan</h2>

<a id="schemavirtualwalletloan"></a>
<a id="schema_VirtualWalletLoan"></a>
<a id="tocSvirtualwalletloan"></a>
<a id="tocsvirtualwalletloan"></a>

```json
{
  "id": 0,
  "walletId": 0,
  "createTime": "2019-08-24T14:15:22Z",
  "repayTime": "2019-08-24T14:15:22Z",
  "loanAmount": 0
}

```

### Attribute

|Name|Type|Required|Restrictions|Title|Description|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||none|
|walletId|integer(int64)|false|none||虚拟钱包id|
|createTime|string(date-time)|false|none||贷款时间|
|repayTime|string(date-time)|false|none||还款时间|
|loanAmount|number|false|none||金额|

