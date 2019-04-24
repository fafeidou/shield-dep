package com.shield.model.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class QueryResponseResult<CourseInfo> extends ResponseResult {

    QueryResult queryResult;

    public QueryResponseResult(ResultCode resultCode,QueryResult queryResult){
        super(resultCode);
       this.queryResult = queryResult;
    }

}
