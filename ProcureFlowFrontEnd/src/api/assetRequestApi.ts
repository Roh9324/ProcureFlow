import axiosClient from './axiosClient';
import type {
  AssetRequest,
  CreateAssetRequestPayload,
  StartReviewPayload,
} from '../types/assetRequest';
import type { RequestHistoryItem } from '../types/requestHistory';

export const createAssetRequest = async (
  data: CreateAssetRequestPayload,
): Promise<AssetRequest> => {
  const response = await axiosClient.post<AssetRequest>('/api/asset-requests', data);
  return response.data;
};

export const getMyAssetRequests = async (): Promise<AssetRequest[]> => {
  const response = await axiosClient.get<AssetRequest[]>('/api/asset-requests/my');
  return response.data;
};

export const getAllAssetRequests = async (): Promise<AssetRequest[]> => {
  const response = await axiosClient.get<AssetRequest[]>('/api/asset-requests/all');
  return response.data;
};

export const startAssetRequestReview = async (
  requestId: number,
  data: StartReviewPayload,
): Promise<AssetRequest> => {
  const response = await axiosClient.put<AssetRequest>(
    `/api/asset-requests/${requestId}/start-review`,
    data,
  );
  return response.data;
};

export const sendForFinalApproval = async (requestId: number): Promise<AssetRequest> => {
  const response = await axiosClient.put<AssetRequest>(
    `/api/asset-requests/${requestId}/send-for-final-approval`,
  );
  return response.data;
};

export const getFinalApprovedRequests = async (): Promise<AssetRequest[]> => {
  const response = await axiosClient.get<AssetRequest[]>('/api/asset-requests/final-approved');
  return response.data;
};

export const getFinalRejectedRequests = async (): Promise<AssetRequest[]> => {
  const response = await axiosClient.get<AssetRequest[]>('/api/asset-requests/final-rejected');
  return response.data;
};

export const notifyEmployee = async (requestId: number): Promise<AssetRequest> => {
  const response = await axiosClient.put<AssetRequest>(
    `/api/asset-requests/${requestId}/notify-employee`,
  );
  return response.data;
};

export const sendOrderToDealer = async (requestId: number): Promise<AssetRequest> => {
  const response = await axiosClient.put<AssetRequest>(
    `/api/asset-requests/${requestId}/send-order-to-dealer`,
  );
  return response.data;
};

export const markDelivered = async (requestId: number): Promise<AssetRequest> => {
  const response = await axiosClient.put<AssetRequest>(
    `/api/asset-requests/${requestId}/mark-delivered`,
  );
  return response.data;
};

export const closeAssetRequest = async (requestId: number): Promise<AssetRequest> => {
  const response = await axiosClient.put<AssetRequest>(
    `/api/asset-requests/${requestId}/close`,
  );
  return response.data;
};

export const getAssetRequestHistory = async (
  requestId: number,
): Promise<RequestHistoryItem[]> => {
  const response = await axiosClient.get<RequestHistoryItem[]>(
    `/api/asset-requests/${requestId}/history`,
  );
  return response.data;
};
