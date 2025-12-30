import torch
import numpy as np
from torch.utils.data import Dataset, DataLoader, TensorDataset, Subset, random_split

class BaseDataset(Dataset):
    def __init__(self, data, train_ratio=0.7, val_ratio=0.2, batch_size=32):
        super().__init__()

        self.data = torch.tensor(np.array(data), dtype=torch.float32).unsqueeze(1)

        full_dataset = TensorDataset(self.data)

        N = len(full_dataset)
        n_train = int(train_ratio * N)
        n_val = int(val_ratio * N)
        n_test = N - n_train - n_val

        self.train_set, self.val_set, self.test_set = random_split(
            full_dataset, [n_train, n_val, n_test]
        )

        self.train_loader = DataLoader(self.train_set, batch_size=batch_size, shuffle=True)
        self.val_loader   = DataLoader(self.val_set, batch_size=batch_size//2, shuffle=False)
        self.test_loader  = DataLoader(self.test_set, batch_size=batch_size//2, shuffle=False)
    
    def get_loaders(self):
        return self.train_loader, self.val_loader, self.test_loader

    def __len__(self):
        return len(self.data)

    def __getitem__(self, idx):
        return self.data[idx]


class LSTMAEDataset(BaseDataset):
    def __init__(self, train_ratio=0.7, val_ratio=0.2, batch_size=32):
        data = self.take_data()
        super().__init__(train_ratio=train_ratio, val_ratio=val_ratio, batch_size=batch_size)

    def take_data():
        pass