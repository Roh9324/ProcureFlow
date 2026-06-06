import axiosClient from './axiosClient';
import type {
  FinalApprovalDecisionPayload,
  FinalApprovalRequest,
} from '../types/finalApproval';

export const getPendingFinalApprovals = async (): Promise<FinalApprovalRequest[]> => {
  const response = await axiosClient.get<FinalApprovalRequest[]>('/api/final-approvals/pending');
  return response.data;
};

export const submitFinalApprovalDecision = async (
  requestId: number,
  data: FinalApprovalDecisionPayload,
): Promise<FinalApprovalRequest> => {
  const response = await axiosClient.put<FinalApprovalRequest>(
    `/api/final-approvals/${requestId}/decision`,
    data,
  );
  return response.data;
};
