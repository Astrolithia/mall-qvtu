/**
 * 通用工具函数模块
 * 
 * 该模块提供项目中常用的工具函数和辅助方法。
 * 主要用于处理日期时间格式化等常见操作。
 * 支持多种格式的数据处理，提高代码复用性。
 * 
 * @module utils/index
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */

/**
 * 日期时间格式化函数
 * 
 * 将时间戳格式化为标准日期时间字符串。
 * 根据flag参数决定是否显示时分秒信息。
 * 支持数字类型和字符串类型的时间戳输入。
 * 
 * @function getFormatTime
 * @param {number|string} dateTime - 需要格式化的时间戳
 * @param {boolean} flag - 是否显示时分秒，true显示时分秒，false仅显示年月日
 * @returns {string} 格式化后的日期时间字符串
 * 
 * @example
 * // 返回: "2023-10-20 14:30:45"
 * getFormatTime(1697787045000, true) 
 * 
 * @example
 * // 返回: "2023-10-20"
 * getFormatTime(1697787045000, false)
 * 
 * @example
 * // 返回: ""
 * getFormatTime(null, true)
 */
export function getFormatTime(dateTime, flag) {
  if(dateTime != null ) {
    //若传入的dateTime为字符串类型，需要进行转换成数值，若不是无需下面注释代码
    var time = parseInt(dateTime)
    var date = new Date(time);
    //获取年份
    var YY = date.getFullYear();
    //获取月份
    var MM = (date.getMonth() + 1 < 10 ? '0'+(date.getMonth() + 1) : date.getMonth() + 1);
    //获取日期
    var DD = (date.getDate() < 10 ? '0'+date.getDate() : date.getDate());
    if(flag) { //flag为true，显示时分秒格式
      //获取小时
      var hh = (date.getHours() < 10 ? '0'+date.getHours() : date.getHours());
      //获取分
      var mm = (date.getMinutes() < 10 ? '0'+date.getMinutes() : date.getMinutes());
      ///获取秒
      var ss = (date.getSeconds() < 10 ? '0'+date.getSeconds() : date.getSeconds());
      //返回时间格式： 2020-11-09 13:14:52
      return YY + '-' + MM + '-' + DD + ' ' + hh + ':' + mm + ':' + ss;
    } else {
      //返回时间格式： 2020-11-09
      return YY + '-' + MM + '-' + DD;
    }
  } else {
    return "";
  }
}
