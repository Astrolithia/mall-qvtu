import { get, post } from '/@/utils/http/axios';
enum URL {
  userList = '/api/classification/list',
  tree = '/api/classification/tree',
  children = '/api/classification/children',
  create = '/api/classification/create',
  update = '/api/classification/update',
  delete = '/api/classification/delete',
}

const listApi = async (params: any) => get<any>({ url: URL.userList, params: params, data: {}, headers: {} });
const treeApi = async (params: any) => get<any>({ url: URL.tree, params: params, data: {}, headers: {} });
const childrenApi = async (params: any) => get<any>({ url: URL.children, params: params, data: {}, headers: {} });
const createApi = async (data: any) =>
  post<any>({ url: URL.create, params: {}, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });
const updateApi = async (params: any, data: any) =>
  post<any>({ url: URL.update, params: params, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });
const deleteApi = async (params: any) => post<any>({ url: URL.delete, params: params, headers: {} });

export { listApi, treeApi, childrenApi, createApi, updateApi, deleteApi };
