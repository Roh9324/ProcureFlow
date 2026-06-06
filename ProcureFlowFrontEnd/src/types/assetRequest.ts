export type AssetPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';

export type AssetRequestStatus =
  | 'REQUEST_SUBMITTED'
  | 'UNDER_HR_REVIEW'
  | 'DEALER_QUOTATION_RECEIVED'
  | 'SENT_FOR_FINAL_APPROVAL'
  | 'FINAL_APPROVED'
  | 'FINAL_REJECTED'
  | 'EMPLOYEE_NOTIFIED'
  | 'ORDER_SENT_TO_DEALER'
  | 'DELIVERED'
  | 'CLOSED'
  | 'SUBMITTED'
  | 'PENDING'
  | 'APPROVED'
  | 'REJECTED'
  | 'CANCELLED';

export interface CreateAssetRequestPayload {
  assetName: string;
  quantity: number;
  reason: string;
  priority: AssetPriority;
  neededByDate: string;
}

export interface AssetRequest {
  id: number;
  assetName: string;
  quantity: number;
  reason?: string;
  priority: AssetPriority;
  neededByDate: string;
  status: AssetRequestStatus | string;
  createdAt: string;
  employeeName?: string;
  employeeEmail?: string;
  hrRemarks?: string;
  finalDecisionReason?: string;
  finalDecision?: string;
  approvalDecision?: string;
  employee?: {
    name?: string;
    email?: string;
  };
  user?: {
    name?: string;
    email?: string;
  };
}

export interface StartReviewPayload {
  hrRemarks: string;
}
