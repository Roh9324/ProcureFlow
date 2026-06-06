import type { FinalApprovalRequest } from '../../types/finalApproval';

export const getApprovalRequestId = (request: FinalApprovalRequest) =>
  request.requestId ?? request.assetRequestId ?? request.id ?? 0;

export const getApprovalEmployeeName = (request: FinalApprovalRequest) =>
  request.employeeName ?? request.employee?.name ?? '-';

export const getApprovalEmployeeEmail = (request: FinalApprovalRequest) =>
  request.employeeEmail ?? request.employee?.email ?? '-';

export const getApprovalDealerName = (request: FinalApprovalRequest) =>
  request.dealerName ?? request.dealerQuotation?.dealerName ?? '-';

export const getApprovalQuotedPrice = (request: FinalApprovalRequest) =>
  request.quotedPrice ?? request.dealerQuotation?.quotedPrice;

export const getApprovalDeliveryDays = (request: FinalApprovalRequest) =>
  request.deliveryDays ?? request.dealerQuotation?.deliveryDays;

export const getApprovalWarranty = (request: FinalApprovalRequest) =>
  request.warrantyDetails ?? request.dealerQuotation?.warrantyDetails ?? '-';

export const getApprovalDealerRemarks = (request: FinalApprovalRequest) =>
  request.dealerRemarks ?? request.dealerQuotation?.dealerRemarks ?? '-';
