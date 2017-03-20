/*
 * Copyright (C) 2015 Ihsan Isik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.timeface.circle.baby.views;

import java.util.HashMap;
import java.util.Map;

import cn.timeface.circle.baby.R;

/**
 * HTTP error status codes.
 */
public class HttpStatusCodes {
    public static final int UNKNOWN_ERROR = -8;
    public static final int PARSE_ERROR = -7;
    public static final int NO_MORE_INFO = -6;
    public static final int NO_MORE_DATA = -5;
    public static final int GET_ALL_MESSAGE = -4;
    public static final int NO_MESSAGE = -3;
    public static final int LOADING = -2;
    public static final int NO_CONNECT = -1;
    public static final int FINISH = 0;

    public static final int CODE_400 = 400;
    public static final int CODE_401 = 401;
    public static final int CODE_402 = 402;
    public static final int CODE_403 = 403;
    public static final int CODE_404 = 404;
    public static final int CODE_405 = 405;
    public static final int CODE_406 = 406;
    public static final int CODE_407 = 407;
    public static final int CODE_408 = 408;
    public static final int CODE_409 = 409;
    public static final int CODE_410 = 410;
    public static final int CODE_411 = 411;
    public static final int CODE_412 = 412;
    public static final int CODE_413 = 413;
    public static final int CODE_414 = 414;
    public static final int CODE_415 = 415;
    public static final int CODE_416 = 416;
    public static final int CODE_417 = 417;

    public static final int CODE_500 = 500;
    public static final int CODE_501 = 501;
    public static final int CODE_502 = 502;
    public static final int CODE_503 = 503;
    public static final int CODE_504 = 504;
    public static final int CODE_505 = 505;

    public static Map<Integer, Integer> getCodesMap() {
        Map<Integer, Integer> mCodes = new HashMap<Integer, Integer>(10);

        mCodes.put(PARSE_ERROR, R.string.state_error_parse_error);
        mCodes.put(NO_MORE_INFO, R.string.no_more_info);
        mCodes.put(NO_MORE_DATA, R.string.no_more_data);
        mCodes.put(GET_ALL_MESSAGE, R.string.get_all_message);
        mCodes.put(NO_MESSAGE, R.string.no_message);
        mCodes.put(LOADING, R.string.loading);
        mCodes.put(NO_CONNECT, R.string.no_connect);
        mCodes.put(FINISH, R.string.finish);

        mCodes.put(CODE_400, R.string.CODE_400);
        mCodes.put(CODE_401, R.string.CODE_401);
        mCodes.put(CODE_402, R.string.CODE_402);
        mCodes.put(CODE_403, R.string.CODE_403);
        mCodes.put(CODE_404, R.string.CODE_404);
        mCodes.put(CODE_405, R.string.CODE_405);
        mCodes.put(CODE_406, R.string.CODE_406);
        mCodes.put(CODE_407, R.string.CODE_407);
        mCodes.put(CODE_408, R.string.CODE_408);
        mCodes.put(CODE_409, R.string.CODE_409);
        mCodes.put(CODE_410, R.string.CODE_410);
        mCodes.put(CODE_411, R.string.CODE_411);
        mCodes.put(CODE_412, R.string.CODE_412);
        mCodes.put(CODE_413, R.string.CODE_413);
        mCodes.put(CODE_414, R.string.CODE_414);
        mCodes.put(CODE_415, R.string.CODE_415);
        mCodes.put(CODE_416, R.string.CODE_416);
        mCodes.put(CODE_417, R.string.CODE_417);

        mCodes.put(CODE_500, R.string.CODE_500);
        mCodes.put(CODE_501, R.string.CODE_501);
        mCodes.put(CODE_502, R.string.CODE_502);
        mCodes.put(CODE_503, R.string.CODE_503);
        mCodes.put(CODE_504, R.string.CODE_504);
        mCodes.put(CODE_505, R.string.CODE_505);

        return mCodes;
    }

    public static int getMessage(int state) {
        switch (state) {
            case PARSE_ERROR:
                return R.string.state_error_parse_error;
            case NO_MORE_INFO:
                return R.string.no_more_info;
            case NO_MORE_DATA:
                return R.string.no_more_data;
            case GET_ALL_MESSAGE:
                return R.string.get_all_message;
            case NO_MESSAGE:
                return R.string.no_message;
            case LOADING:
                return R.string.loading;
            case NO_CONNECT:
                return R.string.no_connect;
            case FINISH:
                return R.string.finish;
            case CODE_400:
                return R.string.CODE_400;
            case CODE_401:
                return R.string.CODE_401;
            case CODE_402:
                return R.string.CODE_402;
            case CODE_403:
                return R.string.CODE_403;
            case CODE_404:
                return R.string.CODE_404;
            case CODE_405:
                return R.string.CODE_405;
            case CODE_406:
                return R.string.CODE_406;
            case CODE_407:
                return R.string.CODE_407;
            case CODE_408:
                return R.string.CODE_408;
            case CODE_409:
                return R.string.CODE_409;
            case CODE_410:
                return R.string.CODE_410;
            case CODE_411:
                return R.string.CODE_411;
            case CODE_412:
                return R.string.CODE_412;
            case CODE_413:
                return R.string.CODE_413;
            case CODE_414:
                return R.string.CODE_414;
            case CODE_415:
                return R.string.CODE_415;
            case CODE_416:
                return R.string.CODE_416;
            case CODE_417:
                return R.string.CODE_417;
            case CODE_500:
                return R.string.CODE_500;
            case CODE_501:
                return R.string.CODE_501;
            case CODE_502:
                return R.string.CODE_502;
            case CODE_503:
                return R.string.CODE_503;
            case CODE_504:
                return R.string.CODE_504;
            case CODE_505:
                return R.string.CODE_505;

            default:
                return R.string.UNKNOWN_ERROR;
        }
    }
}