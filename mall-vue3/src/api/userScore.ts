import { get, post } from '/@/utils/http/axios';

enum URL {
  list = '/api/userScore/list',
  total = '/api/userScore/total',
}

/**
 * 获取用户积分记录列表
 * @param params 包含userId参数
 */
export const getScoreListApi = async (params: any) => 
  get<any>({ url: URL.list, params: params, data: {}, headers: {} });

/**
 * 获取用户总积分
 * @param params 包含userId参数
 */
export const getTotalScoreApi = async (params: any) => 
  get<any>({ url: URL.total, params: params, data: {}, headers: {} }); 