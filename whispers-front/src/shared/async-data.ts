
export type AsyncData<T> =
  { status: 'loading' } |
  { status: 'loaded', data: T } |
  { status: 'error', message?: string };
