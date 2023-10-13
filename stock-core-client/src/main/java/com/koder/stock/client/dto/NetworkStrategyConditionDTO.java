package com.koder.stock.client.dto;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NetworkStrategyConditionDTO {
    /**
     * 最低交易价格
     */
    private String minimumPrice;
    /**
     * 最高交易价格
     */
    private String maximumPrice;
    /**
     * 下跌百分比时购入
     */
    private String buyInDropPt;
    /**
     * 止盈百分比卖出
     */
    private String sellOutRisePt;
    /**
     * 参考价格类型
     * @see ReferencePriceType
     */
    private String referencePriceType;
    /**
     * 最多的没完成的交易订单个数，超过这个数量不再下单
     */
    private Integer maxUnfinishedOrderCount;
    /**
     * 委托数量
     */
    private Integer entrustCount;
    /**
     * 最大持仓数量
     */
    private Integer maxHoldingCount;
    /**
     * 是否重新创建
     * */
    private Boolean reCreate;

    public static void main(String args []){

        NetworkStrategyConditionDTO networkStrategyConditionDTO = NetworkStrategyConditionDTO.builder()
                .buyInDropPt("0.02").sellOutRisePt("0.02").maximumPrice("130").minimumPrice("70")
                .maxUnfinishedOrderCount(3)
                .entrustCount(100)
                .maxHoldingCount(10000)
                .reCreate(true)
                .build();

        System.out.println(JSON.toJSONString(networkStrategyConditionDTO));
    }

}
