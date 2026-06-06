export interface DealerQuotationPayload {
  dealerName: string;
  quotedPrice: number;
  deliveryDays: number;
  warrantyDetails: string;
  dealerRemarks: string;
}

export interface DealerQuotation extends DealerQuotationPayload {
  id: number;
  createdAt?: string;
}
