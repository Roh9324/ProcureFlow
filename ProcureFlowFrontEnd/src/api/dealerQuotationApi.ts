import axiosClient from './axiosClient';
import type {
  DealerQuotation,
  DealerQuotationPayload,
} from '../types/dealerQuotation';

export const createDealerQuotation = async (
  assetRequestId: number,
  data: DealerQuotationPayload,
): Promise<DealerQuotation> => {
  const response = await axiosClient.post<DealerQuotation>(
    `/api/dealer-quotations/asset-requests/${assetRequestId}`,
    data,
  );
  return response.data;
};

export const getDealerQuotationsForRequest = async (
  assetRequestId: number,
): Promise<DealerQuotation[]> => {
  const response = await axiosClient.get<DealerQuotation[]>(
    `/api/dealer-quotations/asset-requests/${assetRequestId}`,
  );
  return response.data;
};
