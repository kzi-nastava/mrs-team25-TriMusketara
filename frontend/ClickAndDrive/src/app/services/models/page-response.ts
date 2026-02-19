// This is universal interface for any pagination that needs to be implemented
export interface PageResponse<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    number: number;
}