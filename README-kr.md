## Lunagram

루나그램은 코스모스 지갑이 내장되어있는 텔레그램 기반 메신저입니다. 루나그램 지갑은 코스모스 아톰 전송, 스테이킹, 그리고 거버넌스 투표 등의 기능을 지원합니다.

유저는 본인의 텔레그램 계정의 `bio` 항목을 코스모스 주소로 변경하여 코스모스 주소와 텔레그램 계정을 연동할 수 있습니다.

**참고: 텔레그램 bio를 코스모스 주소로 바꾸게 되면 모든 텔레그램 유저에게 코스모스 주소가 공개됩니다. 이런 행동에 따른 위험이 존재할 수 있으니 보안에 유의하시기 바랍니다.**

또한 루나그램 월렛은 LMI(Lunagram Message Interface)를 통한 송금 요청을 지원합니다. 봇 개발자는 하단에 설명되어있는 표준 인터페이스를 통해서 봇 기능에 코스모스 결재 기능을 사용할 수 있습니다.

## Lunagram Message Interface, LMI (BETA)

### 개요

루나민트 메시지 인터페이스(LMI, Lunamint Message Interface)는 텔레그램 봇과 외부 애플리케이션이 루나민트 애플리케이션을 통해 송금과 서명을 할 수 있게하는 표준 메시지 포맷입니다.

요청자가 다음과 같은 메시지를 사용자에게 전송하게 되면, Lunagram은 해당 메시지를 분석하여 상황에 맞는 Instance Button을 표시합니다. 사용자는 해당 버튼을 누르면 요청된 LMI message에 따른 송금 또는 서명을 진행할 수 있습니다.

### LMI 포맷

**샘플 LMI 메시지**

```
lmi::1::MX+MCwHCi98HZm/4Zxk03dhX18/opY3CdL2/llztzDZJkMxwm3vXbBArnrxpmxS3l8UT3r4Ya4EDBu9wuL1owE2CkybJxuhLJn9hf13M1/VayKJLH0GLaekQmGtm1wLEh1PYd4Ne3OZp+/fWAQITjoOfffiFlwlY4htSl5h2zQHQ6QulURGnwf4iR2TWJKnXAM46v1EL9qOOkIU5TfBdNHLPqyDdgvdawDKcZjbx2M0W3bpZiPxDYASBVuyAxFR2MFfnidRnQ5neS1thW1vYRhjLi5aOyXAUHtUSXhNHCElnOxKzTI7zfd2vFB/k31pLqRgcAMUuw7fgGZilSVoFX2CdFn8gcKNVEA6STXU+lHMV2OxvFsnwBoMhSv9mBxsloJot+Q3inj3fV5LX+5FT43MpsHAxeCR26YNtTCjafSU=
```

위 LMI 메시지는 3개의 파트로 나뉩니다: `lmi(프리픽스)::1(버전)::message(암호화된 메시지)`

**참고**
1. LMI 메시지의 포맷은 위에 정의된 규칙을 따라야합니다.
2. LMI message 내의 `message` 필드는 AES/CBC/PKCS5Padding로 암호화하여야 합니다. (Encryption key = `lunagram`, Encryption iv = `evqndl&wgvhvaoz!`)

LMI 메시지의 `message` 필드는 다음과 같은 포맷을 이용해주세요:

```{
 "action": "send",
 "requester_t_id": “lunagrambot”,
 "tx": {
  “from”: "cosmos17v0fff40qfwp8l8ruhyjuvh39t8j7qarhkwjpd”,
  "to": "cosmos17v0fff40qfwp8l8ruhyjuvh39t8j7qarhkwjpd",
  "denom": “stake”,
  "amount": “10”,
  "memo": “blah~blah~”
 },
 "callback": {
  "url": "https://lunatestcallback/",
  "endpoint": "deposit“,
  "custom_fields": {
   “custom_field1”: “It’s custom field1”,
   “custom_field2”: “It’s custom field2”
  }
 }
}
```


필드 | 타입 | 설명
------|------|---------
action | String | `send` 또는 `sign`
requester_t_id | String | LMI message를 보내는 사람의 텔레그램 ID
tx.from | String | (선택) 토큰을 보내는 유저의 코스모스 주소
tx.to | String | 토큰을 받는 유저의 코스모스 주소
tx.denom | String | 토큰 식별자
tx.amount | String | 토큰 수량
tx.memo | String | (선택) 트랜잭션의 메모
callback.url | String | 사용자의 action이 완료된 후 callback을 받을 callback base url. 이 필드의 끝은 반드시 `/`로 끝나야 합니다. 콜백은 https 주소만 지원합니다.
callback.endpoint | String | Callback url의 endpoint. `usr/deposit` 처럼 endpoint의 full path를 기입해주세요.
callback.custom_field | json object / any type | 사용자의 action이 완료된 후, 루나그램 앱은 `custom_field` 내용을 지정된 callback url에 전송합니다.

### LMI Callback

**HTTPS / POST / Content-Type: application/json**

사용자가 액션을 완료한 경우, Lunagram 애플리케이션은 `custom_field` 내용을 `lmi_version:1`과 함께 지정된 url에 callback으로 전송합니다.

만약 `custom_field`에 포함된 메시지가 다음과 같을 경우:

```
"custom_field": {
 “custom_field1”: “It’s custom field1”,
 “custom_field2”: “It’s custom field2”
}
```

Callback으로 받게 될 데이터는 다음과 같습니다:

```
{
 “lmi_version”:1,
 “custom_field1”: “It’s custom field1”,
 “custom_field2”: “It’s custom field2”
}
```

### 경고

Callback은 전송 성공을 보장하지 않습니다. 토큰 전송의 경우, callback은 이중 확인 용도로만 사용되어야 합니다. 모든 토큰 전송 결과는 블록체인에 기록된 데이터를 통해 확인하셔야 합니다.
