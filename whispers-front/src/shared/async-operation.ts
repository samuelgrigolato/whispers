
export type AsyncOperation =
  { status: 'idle' } |
  { status: 'executing' };

export type AsyncOperationResult<T> = 
  { status: 'success', data: T } |
  { status: 'error', message?: string };

export type AsyncOperationEmptyResult = AsyncOperationResult<undefined>;
