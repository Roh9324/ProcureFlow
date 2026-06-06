import type { AssetPriority } from './assetRequest';

export type FinalApprovalDecision = 'APPROVED' | 'REJECTED';

export interface FinalApprovalDecisionPayload {
  decision: FinalApprovalDecision;
  reason: string;
}

export interface FinalApprovalRequest {
  id?: number;
  requestId?: number;
  assetRequestId?: number;
  employeeName?: string;
  employeeEmail?: string;
  assetName: string;
  quantity: number;
  reason?: string;
  priority: AssetPriority | string;
  neededByDate: string;
  dealerName?: string;
  quotedPrice?: number;
  deliveryDays?: number;
  warrantyDetails?: string;
  dealerRemarks?: string;
  dealerQuotation?: {
    dealerName?: string;
    quotedPrice?: number;
    deliveryDays?: number;
    warrantyDetails?: string;
    dealerRemarks?: string;
  };
  employee?: {
    name?: string;
    email?: string;
  };
}
