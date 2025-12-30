import torch

class BaseLoss(object):
    key_names = None
    def __init__(self):
        if self.key_names == None:
            raise NotImplementedError("Losses subclasses must implement `key_names` attribute")

        if 'total' not in self.key_names:
            raise NotImplementedError("The key `total` must be present for backdrop")


class LSTMAELoss(BaseLoss):
    key_names = ('total')

    def __init__(self, parameters):
        self.key_names = LSTMAELoss.key_names
        super().__init__()

    def __call__(self, _):
        loss_total = _

        loss = {
                'total': loss_total
               }
        
        return loss